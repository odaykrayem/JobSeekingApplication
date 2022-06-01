package com.example.jobseekingapplication.jobs.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JobsJobsAdapter extends RecyclerView.Adapter<JobsJobsAdapter.ViewHolder> {

    Context context;
    private List<JobVacancy> list;
    public NavController navController;
    ProgressDialog pDialog;
    public JobsJobsAdapter(Context context, ArrayList<JobVacancy> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public JobsJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_company_job, parent, false);
        JobsJobsAdapter.ViewHolder viewHolder = new JobsJobsAdapter.ViewHolder(listItem);
        navController = Navigation.findNavController(parent);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull JobsJobsAdapter.ViewHolder holder, int position) {

        JobVacancy item = list.get(position);

        holder.jobPositionTitle.setText(item.getJobPositionTitle());
        holder.requiredExperience.setText(item.getRequiredExperience());
        holder.workType.setText(item.getWorkType());
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        holder.delete.setOnClickListener(v->{
            LayoutInflater factory = LayoutInflater.from(context);
            final View view1 = factory.inflate(R.layout.dialog_delete_job_vacancy, null);
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            dialog.setView(view1);
            dialog.setCanceledOnTouchOutside(true);

            TextView yes = view1.findViewById(R.id.yes_btn);
            TextView no = view1.findViewById(R.id.no_btn);
            yes.setOnClickListener(l->{
                //Interface is in JobsOpportunities Fragment
                deleteJob(String.valueOf(item.getId()), position);
                dialog.dismiss();
            });

            no.setOnClickListener(l->{
                dialog.dismiss();
            });
            dialog.show();

        });
        holder.update.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putSerializable("job", item);
            navController.navigate(R.id.action_ToUdpateJobVacancy, bundle);
        });
    }

    private void deleteJob(String jobOpportunityId, int position) {

        pDialog.show();

        String url = Urls.DELETE_JOB;
        String advertiser_id = String.valueOf(SharedPrefManager.getInstance(context).getUserId());
        AndroidNetworking.post(url)
                .addBodyParameter("job_id", advertiser_id)
                .addBodyParameter("job_id", jobOpportunityId)
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
                                Toast.makeText(context, context.getResources().getString(R.string.job_delete), Toast.LENGTH_SHORT).show();
                                list.remove(position);
                                notifyItemRemoved(position);
                            }else{
                                Toast.makeText(context, context.getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            Log.e("delete catch", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Log.e("deleteerror", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(context, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("advertiser_id")) {
                                Toast.makeText(context, data.getJSONArray("advertiser_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("job_opportunity_id")) {
                                Toast.makeText(context, data.getJSONArray("job_opportunity_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView jobPositionTitle, requiredExperience, workType;
        public Button delete, update;

        public ViewHolder(View itemView) {
            super(itemView);
            this.jobPositionTitle = itemView.findViewById(R.id.job_position_title);
            this.requiredExperience = itemView.findViewById(R.id.required_experience);
            this.workType = itemView.findViewById(R.id.work_type);
            this.delete = itemView.findViewById(R.id.delete);
            this.update = itemView.findViewById(R.id.update);
        }
    }


}
