package com.example.jobseekingapplication.jobseeker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.LoginActivity;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.company.CompanySignUpActivity;
import com.example.jobseekingapplication.jobseeker.adapters.SkillsAdapter;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.model.Skill;
import com.example.jobseekingapplication.utils.FilePath;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import com.example.jobseekingapplication.utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class JobSeekerSignUpActivity extends AppCompatActivity {

    EditText mNameET, mEmailET, mPasswordET, mPhoneET, mEducationET, mWorkExperienceET, mLanguageET, mSummaryET;
    String name, email, password, phone, education, workExperience, language, summary;
    Button  mSignUpBtn,mUploadCVBtn, mGoToCompanySignUpBtn, mGoToLoginBtn;
    ProgressDialog pDialog;
    AppCompatSpinner mSkillsSpinner;

    ArrayList<Skill> skillsList ;
    int selectedSkillId = -1;
    boolean cvSelected= false;

    private static final int PICK_PDF_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    Uri pdfUri;

    String filePath;
    String verificationCode;
    AlertDialog verificationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_sign_up);

        mNameET = findViewById(R.id.name);
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mPhoneET = findViewById(R.id.phone);
        mEducationET = findViewById(R.id.education);
        mWorkExperienceET =findViewById(R.id.work_experience);
        mLanguageET = findViewById(R.id.language);
        mSummaryET = findViewById(R.id.summary);

        mSignUpBtn = findViewById(R.id.btnSignup);
        mUploadCVBtn = findViewById(R.id.uploadCV);
        mGoToCompanySignUpBtn = findViewById(R.id.btnLinkToSignupCompany);
        mGoToLoginBtn = findViewById(R.id.btnLinkToLoginScreen);
        mSkillsSpinner = findViewById(R.id.skills_chooser);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);



        getSkills();

        mGoToLoginBtn.setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        mGoToCompanySignUpBtn.setOnClickListener(v->{
            startActivity(new Intent(this, CompanySignUpActivity.class));
            finish();
        });
        mSignUpBtn.setOnClickListener(v->{
            if(Validation.validateInput(this, mNameET, mEmailET, mPasswordET, mPhoneET, mEducationET, mWorkExperienceET, mLanguageET, mSummaryET)){
               if(cvSelected){
                   if(selectedSkillId == -1){
                       Toast.makeText(this, getResources().getString(R.string.please_choose_skill), Toast.LENGTH_SHORT).show();

                   }else{
                       signUp();

                   }
               }else{
                   Toast.makeText(this, getResources().getString(R.string.please_choose_cv), Toast.LENGTH_SHORT).show();
               }
            }
        });
        mUploadCVBtn.setOnClickListener(v->{
            requestRead();
        });
    }

    //..................Methods for File Chooser.................
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openFileChooser();
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            Log.d("filePath", String.valueOf(pdfUri));

//            Uri picUri = pdfUri;
            Log.d("filePath", String.valueOf(pdfUri));

            filePath = FilePath.getPath(this,pdfUri);
            if (filePath != null) {
                Log.d("filePath", filePath);
                Toast.makeText(this, "started searching", Toast.LENGTH_SHORT).show();
                cvSelected = true;
                mUploadCVBtn.setBackground(getResources().getDrawable(R.drawable.bg_cv_selected));
            }
            else
            {
                Toast.makeText(this,"no file selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signUp() {
        mSignUpBtn.setEnabled(false);
        String url = Urls.REGISTER_JOBSEEKER;
        pDialog.show();
        name = mNameET.getText().toString().trim();
        phone = mPhoneET.getText().toString().trim();
        email = mEmailET.getText().toString().trim();
        password = mPasswordET.getText().toString().trim();
        education = mEducationET.getText().toString().trim();
        workExperience = mWorkExperienceET.getText().toString().trim();
        language = mLanguageET.getText().toString().trim();
        summary = mSummaryET.getText().toString().trim();

        AndroidNetworking.upload(url)
                .addMultipartFile("cv", new File(filePath))
                .addMultipartParameter("name", name)
                .addMultipartParameter("email", email)
                .addMultipartParameter("password", password)
                .addMultipartParameter("phone", phone)
                .addMultipartParameter("education", education)
                .addMultipartParameter("work_experience", workExperience)
                .addMultipartParameter("language", language)
                .addMultipartParameter("summary", summary)
                .addMultipartParameter("skill_id", String.valueOf(selectedSkillId))
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
                            JSONObject skill_data = userJson.getJSONObject("skill");

                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                SharedPrefManager.getInstance(JobSeekerSignUpActivity.this).jobSeekerLogin(
                                        new JobSeeker(
                                                Integer.parseInt(userJson.getString("id")),
                                                userJson.getString("name"),
                                                userJson.getString("email"),
                                                userJson.getString("phone"),
                                                userJson.getString("education"),
                                                userJson.getString("work_experience"),
                                                userJson.getString("language"),
                                                skill_data.getString("skill"),
                                                Integer.parseInt(skill_data.getString("id")),
                                                userJson.getString("summary"),
                                                userJson.getString("cv")
                                        )
                                );
                                verificationCode = userJson.getString("status");
                                Log.e("code", verificationCode);

                                verifyEmail(verificationCode);
                            }
                            pDialog.dismiss();
                            mSignUpBtn.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("registerjobseeker catch", e.getMessage());
                            pDialog.dismiss();
                            mSignUpBtn.setEnabled(true);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mSignUpBtn.setEnabled(true);
                        Log.e("registerjobseekererror", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(JobSeekerSignUpActivity.this, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("cv")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("cv").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("email")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("email").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("password")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("password").toString(), Toast.LENGTH_SHORT).show();
                            }

                            if (data.has("name")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("name").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("phone")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("phone").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("education")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("education").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("work_experience")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("work_experience").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("language")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("language").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("summary")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("summary").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("skill_id")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("skill_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void verifyEmail(String verificationCode) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.dialog_code_verify, null);
        verificationDialog = new AlertDialog.Builder(this).create();
        verificationDialog.setView(view);
        verificationDialog.setCanceledOnTouchOutside(true);
        verificationDialog.setCancelable(false);


        EditText code = view.findViewById(R.id.code);
        Button verify = view.findViewById(R.id.btn_verify);
        verify.setOnClickListener(v -> {
            if (!code.getText().toString().trim().isEmpty()) {
                String codeFromUser = code.getText().toString().trim();
                if(codeFromUser.equals(verificationCode)){
                    Toast.makeText(this, getResources().getString(R.string.code_correct), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(JobSeekerSignUpActivity.this, JobSeekerMain.class));
                    finish();
                }else{
                    Toast.makeText(this, getResources().getString(R.string.code_not_correct), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        verificationDialog.show();
    }

    public void getSkills(){
        String url = Urls.GET_SKILLS;
        skillsList = new ArrayList<Skill>();
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
                                    skillsList.add(
                                            new Skill(
                                                    Integer.parseInt(obj.getString("id")),
                                                    obj.getString("skill")
                                                    )
                                    );
                                }
                                SkillsAdapter skillsAdapter = new SkillsAdapter(JobSeekerSignUpActivity.this, R.layout.support_simple_spinner_dropdown_item, skillsList);
                                mSkillsSpinner.setAdapter(skillsAdapter);
                                mSkillsSpinner.setSelection(skillsAdapter.getCount());
                                mSkillsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        selectedSkillId = skillsList.get(position).getId();
                                        Log.e("selected Skill", skillsList.get(position).getSkill());
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                Toast.makeText(JobSeekerSignUpActivity.this, getResources().getString(R.string.data_loaded), Toast.LENGTH_SHORT).show();


                            }else{
                                Toast.makeText(JobSeekerSignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            Log.e("jobs catch", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        Log.e("jobs anerror",error.getErrorBody());
                    }
                });
    }
}