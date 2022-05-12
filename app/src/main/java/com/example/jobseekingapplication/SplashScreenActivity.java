package com.example.jobseekingapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.jobseekingapplication.company.CompanyMain;
import com.example.jobseekingapplication.jobseeker.JobSeekerMain;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;

public class SplashScreenActivity extends AppCompatActivity {
    public static final int TIME_TO_START = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn()) {
                    if (SharedPrefManager.getInstance(SplashScreenActivity.this).getUserType() == Constants.USER_TYPE_JOB_SEEKER) {
                        startActivity(new Intent(SplashScreenActivity.this, JobSeekerMain.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, CompanyMain.class));
                    }
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, TIME_TO_START);
    }
}