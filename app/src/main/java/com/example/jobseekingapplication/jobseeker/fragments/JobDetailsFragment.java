package com.example.jobseekingapplication.jobseeker.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.OnApplyBtnClicked;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;

public class JobDetailsFragment extends Fragment {

    Context context;
    NavController navController;
    TextView mJobPositionTitleTV, mRequiredExperienceTV, mWorkTypeTV, mWorkTimeTV, mSalaryRangeTV, mRequiredSkillTV;
    JobVacancy jobVacancy;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public JobDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            jobVacancy = (JobVacancy) getArguments().getSerializable(Constants.KEY_JOB_VACANCY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mJobPositionTitleTV = view.findViewById(R.id.job_position_title);
        mRequiredExperienceTV = view.findViewById(R.id.required_experience);
        mWorkTypeTV = view.findViewById(R.id.work_type);
        mWorkTimeTV = view.findViewById(R.id.work_time);
        mSalaryRangeTV = view.findViewById(R.id.salary_range);
        mRequiredSkillTV = view.findViewById(R.id.required_skills);

        if(jobVacancy != null){
            mJobPositionTitleTV.setText(jobVacancy.getJobPositionTitle());
            mRequiredExperienceTV.setText(jobVacancy.getRequiredExperience());
            mWorkTypeTV.setText(jobVacancy.getWorkType());
            mWorkTimeTV.setText(jobVacancy.getWorkTime());
            mSalaryRangeTV.setText(jobVacancy.getSalaryRange());
            mRequiredSkillTV.setText(jobVacancy.getRequiredSkills());

        }

    }
}