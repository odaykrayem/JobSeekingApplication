package com.example.jobseekingapplication.jobseeker.fragments;

import android.content.Context;
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
import android.widget.TextView;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

public class JobSeekerProfileFragment extends Fragment {

    Context context;

    TextView mNameTV, mPhoneTV, mEmailTV,skillsTV, mEducationTV, mWorkExperienceTV, mLanguageTV, mSummaryTV;
    Button mShowCVBtn, mUpdateProfileBtn;
    NavController navController;
    JobSeeker jobSeeker;
    Boolean fromCompany = false;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public JobSeekerProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            jobSeeker = (JobSeeker) getArguments().getSerializable("jobseeker");
            fromCompany = true;
        }else{
            jobSeeker = SharedPrefManager.getInstance(context).getJobSeekerData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_seeker_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNameTV= view.findViewById(R.id.name);
        mPhoneTV= view.findViewById(R.id.phone);
        mEmailTV= view.findViewById(R.id.email);
        mEducationTV= view.findViewById(R.id.education);
        mWorkExperienceTV= view.findViewById(R.id.work_experience);
        skillsTV= view.findViewById(R.id.skills);
        mLanguageTV= view.findViewById(R.id.language);
        mSummaryTV= view.findViewById(R.id.summary);
        mShowCVBtn= view.findViewById(R.id.show_cv);
        mUpdateProfileBtn= view.findViewById(R.id.update);

        if(fromCompany){
            mUpdateProfileBtn.setVisibility(View.GONE);
        }else{
            mUpdateProfileBtn.setVisibility(View.VISIBLE);
        }

        mNameTV.setText(jobSeeker.getName());
        mPhoneTV.setText(jobSeeker.getPhone());
        mEmailTV.setText(jobSeeker.getEmail());
        mEducationTV.setText(jobSeeker.getEducation());
        mWorkExperienceTV.setText(jobSeeker.getWorkExperience());
        skillsTV.setText(jobSeeker.getSkills());
        mLanguageTV.setText(jobSeeker.getLanguage());
        mSummaryTV.setText(jobSeeker.getSummary());
        navController = Navigation.findNavController(view);

        mUpdateProfileBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_ToUpdateProfile);
        });

        Log.e("profcv", jobSeeker.getCv());
        mShowCVBtn.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_CV_URL, Urls.FILE_BASE_URL+jobSeeker.getCv());
            bundle.putString(Constants.KEY_FILE_NAME, jobSeeker.getName());
            navController.navigate(R.id.action_Profile_ToShowCVFragment, bundle);
        });



    }
}