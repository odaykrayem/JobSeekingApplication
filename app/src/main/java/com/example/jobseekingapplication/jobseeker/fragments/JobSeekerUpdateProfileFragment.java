package com.example.jobseekingapplication.jobseeker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.JobSeekerMain;
import com.example.jobseekingapplication.jobseeker.JobSeekerSignUpActivity;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class JobSeekerUpdateProfileFragment extends Fragment {
    EditText mNameET, mPhoneET, mEducationET, mWorkExperienceET, mLanguageET, mSummaryET;
    String name, phone, education, workExperience, language, summary;

    Button  mUpdateBtn;

    Context context;

    ProgressDialog pDialog;

    NavController navController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public JobSeekerUpdateProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobseeker_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNameET = view.findViewById(R.id.name);
        mPhoneET = view.findViewById(R.id.phone);
        mEducationET = view.findViewById(R.id.education);
        mWorkExperienceET =view.findViewById(R.id.work_experience);
        mLanguageET = view.findViewById(R.id.language);
        mSummaryET = view.findViewById(R.id.summary);

        JobSeeker jobSeeker = SharedPrefManager.getInstance(context).getJobSeekerData();
        mNameET.setText(jobSeeker.getName());
        mPhoneET.setText(jobSeeker.getPhone());
        mEducationET.setText(jobSeeker.getEducation());
        mWorkExperienceET.setText(jobSeeker.getWorkExperience());
        mLanguageET.setText(jobSeeker.getLanguage());
        mSummaryET.setText(jobSeeker.getLanguage());

        mUpdateBtn = view.findViewById(R.id.update);
        navController = Navigation.findNavController(view);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        mUpdateBtn.setOnClickListener(v->{
            update();
        });
    }

    private void update() {
        mUpdateBtn.setEnabled(false);
        pDialog.show();
        String url = Urls.UPDATE_JOBSEEKER;
        name = mNameET.getText().toString().trim();
        phone = mPhoneET.getText().toString().trim();
        education = mEducationET.getText().toString().trim();
        workExperience = mWorkExperienceET.getText().toString().trim();
        language = mLanguageET.getText().toString().trim();
        summary = mSummaryET.getText().toString().trim();
        String userId = String.valueOf(SharedPrefManager.getInstance(context).getUserId());
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", userId)
                .addBodyParameter("name", name)
                .addBodyParameter("phone", phone)
                .addBodyParameter("education", education)
                .addBodyParameter("work_experience", workExperience)
                .addBodyParameter("language", language)
                .addBodyParameter("summary", summary)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            JSONObject obj = response;
                            String message = obj.getString("message");
                            String userFounded = "User Saved";
                            JSONObject userJson = obj.getJSONObject("data");
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                SharedPrefManager.getInstance(context).jobSeekerUpdate(
                                                userJson.getString("name"),
                                                userJson.getString("phone"),
                                                userJson.getString("education"),
                                                userJson.getString("summary"),
                                                userJson.getString("work_experience"),
                                                userJson.getString("language")

                                );
                                Toast.makeText(context, context.getResources().getString(R.string.updated_success), Toast.LENGTH_SHORT).show();
                                navController.popBackStack();
                            }else{
                                Toast.makeText(context, context.getResources().getString(R.string.error_load), Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                            mUpdateBtn.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("updatejobseeker catch", e.getMessage());
                            pDialog.dismiss();
                            mUpdateBtn.setEnabled(true);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mUpdateBtn.setEnabled(true);
                        Log.e("updatejobseekererror", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(context, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("name")) {
                                Toast.makeText(context, data.getJSONArray("name").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("phone")) {
                                Toast.makeText(context, data.getJSONArray("phone").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("education")) {
                                Toast.makeText(context, data.getJSONArray("education").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("work_experience")) {
                                Toast.makeText(context, data.getJSONArray("work_experience").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("language")) {
                                Toast.makeText(context, data.getJSONArray("language").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("summary")) {
                                Toast.makeText(context, data.getJSONArray("summary").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}