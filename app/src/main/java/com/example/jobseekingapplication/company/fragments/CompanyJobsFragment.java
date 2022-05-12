package com.example.jobseekingapplication.company.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.example.jobseekingapplication.company.adapters.CompanyJobsAdapter;
import com.example.jobseekingapplication.jobseeker.adapters.JobVacanciesAdapter;
import com.example.jobseekingapplication.jobseeker.fragments.JobVacanciesFragment;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompanyJobsFragment extends Fragment  implements  SwipeRefreshLayout.OnRefreshListener{

    Context context;
    ProgressDialog pDialog;
    FloatingActionButton mAddNewJobFAB;
    NavController navController;
    RecyclerView mList;
    ArrayList<JobVacancy> list;
    CompanyJobsAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public CompanyJobsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_jobs, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getJobVacancies();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = view.findViewById(R.id.rv);
        mAddNewJobFAB = view.findViewById(R.id.add_fab);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        navController = Navigation.findNavController(view);
        mAddNewJobFAB.setOnClickListener(v->{
            navController.navigate(R.id.action_ToAddNewJobVacancy);
        });

    }
    private void getJobVacancies(){
        String url = Urls.GET_COMPANY_JOBS;
        list = new ArrayList<JobVacancy>();
        pDialog.show();

        String companyId = String.valueOf(SharedPrefManager.getInstance(context).getCompanyData().getId());
        AndroidNetworking.get(url)
                .addQueryParameter("company_id",companyId)
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
                                    JSONObject company_data= obj.getJSONObject("company");
                                    JSONObject skill_data = obj.getJSONObject("skill");
                                    list.add(
                                            new JobVacancy(
                                                    Integer.parseInt(obj.getString("id")),
                                                    company_data.getString("company_name"),
                                                    obj.getString("job_position_title"),
                                                    skill_data.getString("skill"),
                                                    Integer.parseInt(skill_data.getString("id")),
                                                    obj.getString("required_experience"),
                                                    obj.getString("work_type"),
                                                    obj.getString("work_time"),
                                                    obj.getString("salary_range")
                                            )
                                    );
                                }
                                mAdapter = new CompanyJobsAdapter(context, list);
                                mList.setAdapter(mAdapter);

                                Toast.makeText(context, getResources().getString(R.string.data_loaded), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            pDialog.dismiss();
                            Log.e("jobs catch", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        pDialog.dismiss();
                        Log.e("jobs anerror",error.getErrorBody());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getJobVacancies();
    }
}