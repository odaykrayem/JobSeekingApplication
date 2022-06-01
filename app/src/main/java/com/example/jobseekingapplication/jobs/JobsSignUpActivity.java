package com.example.jobseekingapplication.jobs;

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
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.LoginActivity;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.JobSeekerSignUpActivity;
import com.example.jobseekingapplication.model.Company;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import org.json.JSONException;
import org.json.JSONObject;

public class JobsSignUpActivity extends AppCompatActivity {

    EditText mCompanyNameET, mEmailET, mPasswordET, mAddressET, mDetailsET;
    String companyName, email, password, address, details;
    Button mSignUpBtn, mGoToJobSeekerSignUpBtn, mGoToLoginBtn;
    ProgressDialog pDialog;
    String verificationCode;
    AlertDialog verificationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_sign_up);

        mCompanyNameET = findViewById(R.id.company_name);
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mAddressET = findViewById(R.id.company_address);
        mDetailsET = findViewById(R.id.company_details);

        mSignUpBtn = findViewById(R.id.btnSignup);
        mGoToJobSeekerSignUpBtn = findViewById(R.id.btnLinkToSignUpJobSeeker);
        mGoToLoginBtn = findViewById(R.id.btnLinkToLoginScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        mGoToLoginBtn.setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        mGoToJobSeekerSignUpBtn.setOnClickListener(v->{
            startActivity(new Intent(this, JobSeekerSignUpActivity.class));
            finish();
        });
        mSignUpBtn.setOnClickListener(v->{
            signUp();
        });
    }

    private void signUp() {
        mSignUpBtn.setEnabled(false);
        String url = Urls.REGISTER_COMPANY;
        pDialog.show();
        companyName = mCompanyNameET.getText().toString().trim();
        email = mEmailET.getText().toString().trim();
        password = mPasswordET.getText().toString().trim();
        address = mAddressET.getText().toString().trim();
        details = mDetailsET.getText().toString().trim();

        AndroidNetworking.post(url)
                .addBodyParameter("name", companyName)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("address", address)
                .addBodyParameter("details", details)
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
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                SharedPrefManager.getInstance(JobsSignUpActivity.this).companyLogin(
                                        new Company(
                                                Integer.parseInt(userJson.getString("id")),
                                                userJson.getString("job_name"),
                                                userJson.getString("job_address"),
                                                userJson.getString("job_details"),
                                                userJson.getString("email")
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
                            Log.e("registercompany catch", e.getMessage());
                            pDialog.dismiss();
                            mSignUpBtn.setEnabled(true);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mSignUpBtn.setEnabled(true);
                        Log.e("registercompanyererror", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(JobsSignUpActivity.this, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("email")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("email").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("password")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("password").toString(), Toast.LENGTH_SHORT).show();
                            }

                            if (data.has("name")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("name").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("address")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("address").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("details")) {
                                Toast.makeText(getApplicationContext(), data.getJSONArray("details").toString(), Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(JobsSignUpActivity.this, LoginActivity.class));
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

}