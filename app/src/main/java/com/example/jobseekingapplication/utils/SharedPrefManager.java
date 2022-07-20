package com.example.jobseekingapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jobseekingapplication.model.Jobs;
import com.example.jobseekingapplication.model.JobSeeker;


public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "generalFile";

    //student registration/ login
    private static final String KEY_ID = "keyid";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_EDUCATION = "keyseducation";
    private static final String KEY_WORK_EXPERIENCE = "keyworkexper";
    private static final String KEY_LANGUAGE = "keylanguage";
    private static final String KEY_SKILLS = "keyskills";
    private static final String KEY_SKILL_ID = "keyskillid";
    private static final String KEY_SUMMARY = "keysummary";
    private static final String KEY_CV = "keycv";
    private static final String KEY_DETAILS = "keycompanydetails";
    private static final String KEY_ADDRESS = "keycompanyaddress";
    private static final String KEY_USER_TYPE= "keyusertype";


    private static SharedPrefManager mInstance;
    private static Context context;

    public SharedPrefManager(Context context) {
        SharedPrefManager.context = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will store the student data in shared preferences
    public void jobSeekerLogin(JobSeeker object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, object.getId());
        editor.putString(KEY_NAME, object.getName());
        editor.putString(KEY_EMAIL, object.getEmail());
        editor.putString(KEY_PHONE, object.getPhone());
        editor.putString(KEY_EDUCATION, object.getEducation());
        editor.putString(KEY_SKILLS, object.getSkills());
        editor.putInt(KEY_SKILL_ID, object.getSkillId());
        editor.putString(KEY_SUMMARY, object.getSummary());
        editor.putString(KEY_WORK_EXPERIENCE, object.getWorkExperience());
        editor.putString(KEY_LANGUAGE, object.getLanguage());
        editor.putString(KEY_CV, object.getCv());

        editor.putInt(KEY_USER_TYPE, Constants.USER_TYPE_JOB_SEEKER);

        editor.apply();
    }

    public void jobSeekerUpdate(String name, String phone, String edu, String summ, String we, String lang) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_EDUCATION, edu );
        editor.putString(KEY_SUMMARY, summ);
        editor.putString(KEY_WORK_EXPERIENCE, we);
        editor.putString(KEY_LANGUAGE, lang);

        editor.apply();
    }

    public void jobsLogin(Jobs object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, object.getId());
        editor.putString(KEY_NAME, object.getCompanyName());
        editor.putString(KEY_EMAIL, object.getEmail());
        editor.putString(KEY_DETAILS, object.getCompanyDetails());
        editor.putString(KEY_ADDRESS, object.getCompanyAddress());

        editor.putInt(KEY_USER_TYPE, Constants.USER_TYPE_COMPANY);

        editor.apply();
    }

    public void companyUpdate(String company_name, String company_address, String company_details) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, company_name);
        editor.putString(KEY_DETAILS, company_address);
        editor.putString(KEY_ADDRESS, company_details);

        editor.apply();
    }
    public Jobs getCompanyData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Jobs(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_ADDRESS, null),
                sharedPreferences.getString(KEY_DETAILS, null),
                sharedPreferences.getString(KEY_EMAIL, null)
        );
    }
    //this method will check whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1) != -1;
    }

    //this method will give the logged in user id
    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1);
    }

    //this method will give the logged in user id
    public int getUserType() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_TYPE, -1);
    }

    //this method will give the logged in user
    public JobSeeker getJobSeekerData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new JobSeeker(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_EDUCATION, null),
                sharedPreferences.getString(KEY_WORK_EXPERIENCE, null),
                sharedPreferences.getString(KEY_LANGUAGE, null),
                sharedPreferences.getString(KEY_SKILLS, null),
                sharedPreferences.getInt(KEY_SKILL_ID, -1),
                sharedPreferences.getString(KEY_SUMMARY, null),
                sharedPreferences.getString(KEY_CV, null)
        );
    }


    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }


}
