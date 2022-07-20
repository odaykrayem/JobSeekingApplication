package com.example.jobseekingapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.jobs.JobsMain;
import com.example.jobseekingapplication.jobseeker.JobSeekerMain;
import com.example.jobseekingapplication.jobseeker.JobSeekerSignUpActivity;
import com.example.jobseekingapplication.model.Jobs;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import com.example.jobseekingapplication.utils.Validation;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailET, mPasswordET;
    RadioGroup mAccountType;
    int selectedAccountType;
    Button mLoginBtn, mGoToSignUpBtn, mResetPassword;
    ProgressDialog pDialog;
    AlertDialog resetPasswordDialog, resetPasswordRequestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.btnLogin);
        mGoToSignUpBtn = findViewById(R.id.btnLinkToRegisterScreen);

        mAccountType = findViewById(R.id.account_type);
        selectedAccountType = 1;
        mAccountType.check(R.id.job_seeker);
        mResetPassword = findViewById(R.id.btnLinkToResetPassword);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        mResetPassword.setOnClickListener(v->{
            displayResetPasswordRequestDialog();
        });
        mAccountType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.job_seeker:
                    selectedAccountType = 1;
                    break;
                case R.id.jobs:
                    selectedAccountType = 2;
                    break;
            }
        });
        mGoToSignUpBtn.setOnClickListener(v->{
            startActivity(new Intent(this, JobSeekerSignUpActivity.class));
            finish();
        });
        mLoginBtn.setOnClickListener(v->{
            if(Validation.validateInput(this, mEmailET, mPasswordET)){
                login();
            }
        });
    }

    private void login() {
        mLoginBtn.setEnabled(false);
        pDialog.show();
        String url = Urls.LOG_IN;
        String email = mEmailET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        AndroidNetworking.post(url)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("type", String.valueOf(selectedAccountType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = response;
                            String message = obj.getString("message");
                            String userFounded = "User founded";
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                JSONObject userJson = obj.getJSONObject("data");
                                int userType = userJson.getInt("type");
                                if (userType == Constants.USER_TYPE_JOB_SEEKER) {
                                    JSONObject skill_data = userJson.getJSONObject("skill");

                                    Log.e("cvLog",userJson.getString("cv"));
                                    SharedPrefManager.getInstance(LoginActivity.this).jobSeekerLogin(
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
                                    goToUserMain(userType);
                                } else {
                                    if(userJson.getInt("activated") == 1){
                                        SharedPrefManager.getInstance(LoginActivity.this).jobsLogin(
                                                new Jobs(
                                                        Integer.parseInt(userJson.getString("id")),
                                                        userJson.getString("job_name"),
                                                        userJson.getString("job_address"),
                                                        userJson.getString("job_details"),
                                                        userJson.getString("email")
                                                )
                                        );
                                        goToUserMain(userType);

                                    }else{
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                            Log.e("login", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mLoginBtn.setEnabled(true);
                        Log.e("loginError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("email")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("email").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("type")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("type").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("password")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("password").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private  void resetPasswordRequest(String email){
        pDialog.show();

        String url = Urls.RESET_PASSWORD_REQUEST;

        AndroidNetworking.post(url)
                .addBodyParameter("email", email)
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
                            //if no error in response
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                //getting the user from the response
                                Log.e("c", obj.getString("data"));
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.code_sent), Toast.LENGTH_SHORT).show();
                                resetPasswordRequestDialog.dismiss();
                                displayResetPasswordDialog(email);
                            }else{
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                            Log.e("login", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mLoginBtn.setEnabled(true);
                        Log.e("loginError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("email")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("email").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("password")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("password").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void setNewPassword(String code, String email, String newPass) {

        pDialog.show();

        String url = Urls.SET_NEW_PASSWORD;

        AndroidNetworking.post(url)
                .addBodyParameter("email", email)
                .addBodyParameter("password", newPass)
                .addBodyParameter("code", code)
                .addBodyParameter("type",String.valueOf(selectedAccountType))
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
                            //if no error in response
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                //getting the user from the response

                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.passsword_reset_success), Toast.LENGTH_SHORT).show();
                                resetPasswordDialog.dismiss();
                           }else{
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mLoginBtn.setEnabled(true);
                            pDialog.dismiss();
                            Log.e("login", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mLoginBtn.setEnabled(true);
                        Log.e("loginError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("email")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("email").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("password")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("password").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    
    private void displayResetPasswordDialog(String userEmail) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.dialog_reset_password, null);
        resetPasswordDialog = new AlertDialog.Builder(this).create();
        resetPasswordDialog.setView(view);
        resetPasswordDialog.setCanceledOnTouchOutside(true);


        EditText code = view.findViewById(R.id.code);
        EditText email = view.findViewById(R.id.email);
        email.setText(userEmail);
        EditText newPass = view.findViewById(R.id.new_pass);
        Button btnReset = view.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> {
            if (!code.getText().toString().trim().isEmpty() && !email.getText().toString().trim().isEmpty() && !newPass.getText().toString().trim().isEmpty()) {
                    setNewPassword(code.getText().toString().trim(),
                            email.getText().toString().trim(),
                            newPass.getText().toString().trim()
                    );
            }else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        resetPasswordDialog.show();
    }

    private void displayResetPasswordRequestDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.dialog_reset_password_request, null);
        resetPasswordRequestDialog = new AlertDialog.Builder(this).create();
        resetPasswordRequestDialog.setView(view);
        resetPasswordRequestDialog.setCanceledOnTouchOutside(true);


        EditText email = view.findViewById(R.id.email);
        Button btnSendCode = view.findViewById(R.id.send_code);
        btnSendCode.setOnClickListener(v -> {
            if (!email.getText().toString().trim().isEmpty()) {
                resetPasswordRequest(email.getText().toString().trim());
            }else{
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });
        resetPasswordRequestDialog.show();
    }
    private void goToUserMain(int type) {
        if (type == Constants.USER_TYPE_JOB_SEEKER) {
            startActivity(new Intent(LoginActivity.this, JobSeekerMain.class));
        } else {
            startActivity(new Intent(LoginActivity.this, JobsMain.class));
        }
        finish();
    }
}