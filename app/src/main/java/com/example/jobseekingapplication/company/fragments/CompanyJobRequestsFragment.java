package com.example.jobseekingapplication.company.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.company.adapters.CompanyJobRequestsAdapter;
import com.example.jobseekingapplication.company.adapters.CompanyJobsAdapter;
import com.example.jobseekingapplication.jobseeker.adapters.AppliedJobsAdapter;
import com.example.jobseekingapplication.model.JobApplication;
import com.example.jobseekingapplication.model.JobRequest;
import com.example.jobseekingapplication.model.JobSeeker;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompanyJobRequestsFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

    Context context;
    ProgressDialog pDialog;
    FloatingActionButton mAddNewJobFAB;
    NavController navController;
    RecyclerView mList;
    ArrayList<JobRequest> list;
    CompanyJobRequestsAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
    public CompanyJobRequestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_requests, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getJobRequests();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = view.findViewById(R.id.rv);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
    }

    @Override
    public void onRefresh() {
        getJobRequests();
    }

    private void getJobRequests() {
        String url = Urls.GET_JOB_REQUESTS;
        list = new ArrayList<JobRequest>();
        pDialog.show();
        String companyId = String.valueOf(SharedPrefManager.getInstance(context).getUserId());
        AndroidNetworking.get(url)
                .addQueryParameter("company_id", companyId)
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
                                    JSONObject job_data = obj.getJSONObject("job");
                                    JSONObject jobseeker_data = obj.getJSONObject("seeker");
                                    JSONObject skill_data = jobseeker_data.getJSONObject("skill");
                                    JobRequest jobRequest =    new JobRequest(
                                            Integer.parseInt(obj.getString("id")),
                                            job_data.getString("job_position_title"),
                                            jobseeker_data.getString("name"),
                                            Integer.parseInt(obj.getString("status")),
                                            obj.getString("created_at")
                                    );
                                    jobRequest.setJobSeeker(new JobSeeker(
                                            Integer.parseInt(jobseeker_data.getString("id")),
                                            jobseeker_data.getString("name"),
                                            jobseeker_data.getString("email"),
                                            jobseeker_data.getString("phone"),
                                            jobseeker_data.getString("education"),
                                            jobseeker_data.getString("work_experience"),
                                            jobseeker_data.getString("language"),
                                            skill_data.getString("skill"),
                                            Integer.parseInt(skill_data.getString("id")),
                                            jobseeker_data.getString("summary"),
                                            jobseeker_data.getString("cv")
                                    ));
                                    list.add(
                                         jobRequest
                                    );
                                }
                                mAdapter = new CompanyJobRequestsAdapter(context, list);
                                mList.setAdapter(mAdapter);

                                Toast.makeText(context, getResources().getString(R.string.data_loaded), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, getResources().getString(R.string.error_load), Toast.LENGTH_SHORT).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                            Log.e("appliedjobs catch", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        pDialog.dismiss();
                        Log.e("apppliedjobs anerror",error.getErrorBody());
                    }
                });


    }
}