package com.example.jobseekingapplication.jobseeker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.adapters.JobVacanciesAdapter;
import com.example.jobseekingapplication.jobseeker.OnApplyBtnClicked;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JobVacanciesFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, OnApplyBtnClicked {

    Context context;
    RecyclerView mList;
    ArrayList<JobVacancy> list;
    JobVacanciesAdapter mAdapter;
    ProgressDialog pDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SearchView searchView;
    Button viewAll, recommended;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public JobVacanciesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO search vacancies and filter for recommendation
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_job_vacancies, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getJobVacancies();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = view.findViewById(R.id.rv);
        searchView = view.findViewById(R.id.search);
        viewAll = view.findViewById(R.id.all);
        recommended = view.findViewById(R.id.recommended);

        Log.e("id", String.valueOf(SharedPrefManager.getInstance(context).getUserId()));
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mAdapter != null){
                    mAdapter.getFilter().filter(newText + ":" + Constants.SEARCH);
                }
                return true;
            }
        });
        viewAll.setOnClickListener(v->{
            mAdapter.getFilter().filter("" + ":" + Constants.SEARCH);
        });

        recommended.setOnClickListener(v->{
            mAdapter.getFilter().filter("query" + ":"+"recommended");
        });
    }

    private void getJobVacancies(){
        String url = Urls.GET_JOB_VACANCIES;
        list = new ArrayList<JobVacancy>();
        pDialog.show();

        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            String message = jsonObject.getString("message");
                            String successMessage = "Found";
                            if(message.toLowerCase().contains(successMessage.toLowerCase())){
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    JSONObject company_data= obj.getJSONObject("job");
                                    JSONObject skill_data = obj.getJSONObject("skill");
                                    list.add(
                                            new JobVacancy(
                                                    Integer.parseInt(obj.getString("id")),
                                                    company_data.getString("job_name"),
                                                    obj.getString("job_position_title"),
                                                    skill_data.getString("skill"),
                                                    Integer.parseInt(skill_data.getString("id")),
                                                    obj.getString("required_experience"),
                                                    obj.getString("work_type"),
                                                    obj.getString("work_time"),
                                                    obj.getString("salary_range")
                                            )
                                    );
                                }
                                mAdapter = new JobVacanciesAdapter(context, list, JobVacanciesFragment.this);
                                mList.setAdapter(mAdapter);

                                Toast.makeText(context, getResources().getString(R.string.data_loaded), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                            Log.e("jobs catch", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        pDialog.dismiss();
                        Log.e("jobs anerror",error.getErrorBody());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getJobVacancies();
    }

    @Override
    public void onApplyBtnClicked(int jobVacancyId) {
        applyJobVacancy(jobVacancyId);
    }

    private void applyJobVacancy(int jobVacancyId) {
        pDialog.show();
        String url = Urls.APPLY_FOR_JOB_VACANCY;
        String jobSeekerId = String.valueOf(SharedPrefManager.getInstance(context).getUserId());

        AndroidNetworking.post(url)
                .addBodyParameter("job_seeker_id", jobSeekerId)
                .addBodyParameter("job_id", String.valueOf(jobVacancyId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            JSONObject obj = response;
                            String message = obj.getString("message");
                            String userFounded = "Saved";
                            //if no error in response
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                Toast.makeText(context, context.getResources().getString(R.string.apply_job), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, context.getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            Log.e("apply", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Log.e("applyError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(context, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("student_id")) {
                                Toast.makeText(context, data.getJSONArray("student_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("job_id")) {
                                Toast.makeText(context, data.getJSONArray("job_id").toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}