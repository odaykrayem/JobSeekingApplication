package com.mrmindteam.jobseekingapplication.jobseeker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrmindteam.jobseekingapplication.R;

public class JobSeekerAppliedJobsFragment extends Fragment {

    public JobSeekerAppliedJobsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_seeker_applied_jobs, container, false);
    }
}