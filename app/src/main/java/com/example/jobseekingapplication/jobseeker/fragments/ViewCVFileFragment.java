package com.example.jobseekingapplication.jobseeker.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;


public class ViewCVFileFragment extends Fragment implements OnLoadCompleteListener, OnPageChangeListener, OnPageErrorListener {

    //for writing on storage permission
    private static final int WRITE_EXT_STORAGE_CODE = 1;

    //for binding
    PDFView pdfView;
    BottomNavigationView bottomNav;

    //while pdf is loaded
    ProgressDialog progressDialog;

    //for getting from extras
    String url;
    String fileName;
    int numOfPages;

    SharedPrefManager prefManager;
    float progress;

    //playing sound for capturing image
    MediaPlayer mp;

    //values to get from shared preferences
    // for bookmarks purposes
    int currentPageNumber = 0;

    //pdf file after downloading
    File pdfUrl;

    //controls showing and hiding bottom menu
    OnTapListener onTapListener;
    private boolean tapped = false;
    Context context;

    NavController navController;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public ViewCVFileFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString(Constants.KEY_CV_URL);
            fileName = getArguments().getString(Constants.KEY_FILE_NAME);

        }else{

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_cv_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNav = view.findViewById(R.id.bottom_navigation_bookView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        navController = Navigation.findNavController(view);
        prefManager = SharedPrefManager.getInstance(context);

        //if the pdf is large or not downloaded so it will take time to load
        pdfView = view.findViewById(R.id.pdfView);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait\nFetching pdf...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //showing and hiding bottom tools menu when tapping on screen
        onTapListener = new OnTapListener() {
            @Override
            public boolean onTap(MotionEvent e) {
                if (tapped) {
                    bottomNav.setVisibility(View.INVISIBLE);
                    tapped = false;
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    tapped = true;
                }
                return true;
            }
        };

        FileLoader.with(context)
                .load(url, false) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory("lectures/" + fileName, FileLoader.DIR_INTERNAL)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfUrl = response.getBody();
                        // do something with the file
                        try {
                            pdfView.fromFile(pdfUrl)
                                    .onTap(onTapListener)
                                    .onPageScroll(new OnPageScrollListener() {
                                        @Override
                                        public void onPageScrolled(int page, float positionOffset) {
                                            bottomNav.setVisibility(View.VISIBLE);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bottomNav.setVisibility(View.INVISIBLE);
                                                }
                                            }, 1000);
                                        }
                                    })
                                    .defaultPage(0)
                                    .enableSwipe(true)
                                    .enableAnnotationRendering(true)
                                    .onLoad(ViewCVFileFragment.this)
                                    .onPageChange(ViewCVFileFragment.this)
                                    .scrollHandle(new DefaultScrollHandle(context))
                                    .enableDoubletap(true)
                                    .onPageError(ViewCVFileFragment.this)
                                    .swipeHorizontal(true)
                                    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                                    .fitEachPage(true)
                                    .pageSnap(true)
                                    .pageFling(true)
                                    .spacing(0)
                                    .load();

                            Log.i("FilePath: ", "onLoad: " + pdfUrl);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "" + t.getMessage() + ", File Error", Toast.LENGTH_SHORT).show();
                        navController.popBackStack();
                    }
                });


    }

    @Override
    public void loadComplete(int nbPages) {
        progressDialog.dismiss();
        numOfPages = nbPages;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPageNumber = page + 1;

        progress = (float) currentPageNumber / (float) numOfPages;

        if (progress > 1.0f) {
            progress = 1.0f;
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int navId = item.getItemId();
            switch (navId) {

                case R.id.screen_shot:
                    screenShot();
                    break;

                case R.id.bookmark:
                    //todo save the bookmark here

                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void screenShot() {
        Bitmap b = Bitmap.createBitmap(pdfView.getWidth(), pdfView.getHeight(), Bitmap.Config.ARGB_8888);

        int width, height;
        width = pdfView.getWidth();
        height = pdfView.getHeight();
        Bitmap cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(cs);
        c.drawBitmap(b, 0, 0, null);
        pdfView.draw(c);
        c.setBitmap(cs);


        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/MyPlan");
        dir.mkdir();
        String imagename = time + ".JPEG";
        File file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            cs.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            mp = MediaPlayer.create(context, R.raw.camerashutter);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                }
            });
            //for gallery to be notified of the Image
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
            //todo add string resource for locals
            Toast.makeText(context, "screenshot saved to gallery", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    String[] permission = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    requestPermissions(permission, WRITE_EXT_STORAGE_CODE);
                }
            }
        }
    }



}