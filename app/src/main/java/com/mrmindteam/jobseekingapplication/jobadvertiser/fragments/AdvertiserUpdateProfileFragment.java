package com.mrmindteam.jobseekingapplication.jobadvertiser.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrmindteam.jobseekingapplication.R;

public class AdvertiserUpdateProfileFragment extends Fragment {

    public AdvertiserUpdateProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advertiser_update_profile, container, false);
    }
}