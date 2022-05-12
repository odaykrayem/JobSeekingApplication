package com.example.jobseekingapplication.company.fragments;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.model.Company;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class CompanyUpdateProfileFragment extends Fragment {
    EditText mCompanyName, mCompanyAddress, mCompanyDetails;
    String companyName, companyAddress, companyDetails;

    Button mUpdateBtn;
    Context context;
    ProgressDialog pDialog;

    NavController navController;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
    public CompanyUpdateProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCompanyName = view.findViewById(R.id.company_name);
        mCompanyAddress = view.findViewById(R.id.company_address);
        mCompanyDetails = view.findViewById(R.id.company_details);

        Company company = SharedPrefManager.getInstance(context).getCompanyData();
        mCompanyName.setText(company.getCompanyName());
        mCompanyAddress.setText(company.getCompanyAddress());
        mCompanyDetails.setText(company.getCompanyDetails());

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
        String url = Urls.UPDATE_COMPANY;

        companyName = mCompanyName.getText().toString().trim();
        companyAddress = mCompanyAddress.getText().toString().trim();
        companyDetails = mCompanyDetails.getText().toString().trim();

        String userId = String.valueOf(SharedPrefManager.getInstance(context).getUserId());
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", userId)
                .addBodyParameter("name", companyName)
                .addBodyParameter("address", companyAddress)
                .addBodyParameter("details", companyDetails)
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
                                SharedPrefManager.getInstance(context).companyUpdate(
                                        userJson.getString("company_name"),
                                        userJson.getString("company_address"),
                                        userJson.getString("company_details")

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
                            if (data.has("address")) {
                                Toast.makeText(context, data.getJSONArray("address").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("details")) {
                                Toast.makeText(context, data.getJSONArray("details").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}