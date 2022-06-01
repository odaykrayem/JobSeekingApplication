package com.example.jobseekingapplication.jobseeker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.jobseekingapplication.jobseeker.adapters.AppliedJobsAdapter;
import com.example.jobseekingapplication.jobseeker.adapters.JobVacanciesAdapter;
import com.example.jobseekingapplication.model.JobApplication;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobSeekerAppliedJobsFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    Context context;
    RecyclerView mList;
    ArrayList<JobApplication> list;

    AppliedJobsAdapter mAdapter;
    ProgressDialog pDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public JobSeekerAppliedJobsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_seeker_applied_jobs, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getAppliedJobs();
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
        getAppliedJobs();
    }

    private  void getAppliedJobs(){
        String url = Urls.GET_APPLIED_JOBS;
        list = new ArrayList<JobApplication>();
        pDialog.show();
        String jobSeekerId = String.valueOf(SharedPrefManager.getInstance(context).getUserId());
        AndroidNetworking.get(url)
                .addQueryParameter("job_seeker_id", jobSeekerId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            Log.e("res", response.toString());
                            String message = jsonObject.getString("message");
                            String successMessage = "Found";
                            if(message.toLowerCase().contains(successMessage.toLowerCase())){
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if(!obj.isNull("job")){
                                        JSONObject job_data = obj.getJSONObject("job");
                                        JSONObject company_data = job_data.getJSONObject("job");

                                        list.add(
                                                new JobApplication(
                                                        Integer.parseInt(obj.getString("id")),
                                                        job_data.getString("job_position_title"),
                                                        company_data.getString("job_name"),
                                                        Integer.parseInt(obj.getString("status")),
                                                        obj.getString("created_at")
                                                )
                                        );
                                    }

                                }
                                mAdapter = new AppliedJobsAdapter(context, list);
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