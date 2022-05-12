package com.example.jobseekingapplication.company.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.jobseekingapplication.model.JobApplication;
import com.example.jobseekingapplication.model.JobRequest;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompanyJobRequestsAdapter extends RecyclerView.Adapter<CompanyJobRequestsAdapter.ViewHolder> {

    Context context;
    private List<JobRequest> list;
     AlertDialog acceptDialog;
     AlertDialog rejectDialog;
     ProgressDialog pDialog;
     NavController navController;

    public CompanyJobRequestsAdapter(Context context, ArrayList<JobRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_job_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        navController = Navigation.findNavController(parent);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JobRequest item = list.get(position);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        holder.jobPositionTitle.setText(item.getJobTitle());
        holder.jobSeekerName.setText(item.getJobSeekerName());
        holder.date.setText(item.getDate());
        if(item.getStatus() == Constants.STATUS_PROCESSING){
           holder.status.setVisibility(View.GONE);
            holder.layoutBtns.setVisibility(View.VISIBLE);
        }
        else  if(item.getStatus() == Constants.STATUS_ACCEPTED){
            holder.layoutBtns.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(Constants.STATUS_ACCEPTED_TXT);
            holder.status.setBackground(context.getDrawable(R.drawable.bg_status_accepted));
        }
        else{
            holder.layoutBtns.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(Constants.STATUS_REJECTED_TXT);
            holder.status.setBackground(context.getDrawable(R.drawable.bg_status_rejected));
        }
        holder.accept.setOnClickListener(v->{
            LayoutInflater factory = LayoutInflater.from(context);
            final View view1 = factory.inflate(R.layout.dialog_accept_request, null);
            acceptDialog = new AlertDialog.Builder(context).create();
            acceptDialog.setView(view1);
            acceptDialog.setCanceledOnTouchOutside(true);

            TextView yes = view1.findViewById(R.id.yes_btn);
            TextView no = view1.findViewById(R.id.no_btn);
            yes.setOnClickListener(l->{
                //Interface is in JobsOpportunities Fragment
                changeRequestStatus(String.valueOf(item.getId()), position, Constants.STATUS_ACCEPTED);
                acceptDialog.dismiss();
            });

            no.setOnClickListener(l->{
                acceptDialog.dismiss();
            });
            acceptDialog.show();
        });

        holder.reject.setOnClickListener(v->{
            LayoutInflater factory = LayoutInflater.from(context);
            final View view1 = factory.inflate(R.layout.dialog_reject_request, null);
             rejectDialog = new AlertDialog.Builder(context).create();
            rejectDialog.setView(view1);
            rejectDialog.setCanceledOnTouchOutside(true);

            TextView yes = view1.findViewById(R.id.yes_btn);
            TextView no = view1.findViewById(R.id.no_btn);
            yes.setOnClickListener(l->{
                //Interface is in JobsOpportunities Fragment
                changeRequestStatus(String.valueOf(item.getId()), position, Constants.STATUS_REJECTED);
            });

            no.setOnClickListener(l->{
                rejectDialog.dismiss();
            });
            rejectDialog.show();
        });

        holder.itemView.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putSerializable("jobseeker", item.getJobSeeker());
            navController.navigate(R.id.action_ToShowJobSeekerDetailsFragment, bundle);
        });
    }

    private void changeRequestStatus(String requestId, int position, int newStatus) {
        pDialog.show();
        String url = Urls.CHANGE_REQUEST_STATUS;

        AndroidNetworking.post(url)
                .addBodyParameter("id", requestId)
                .addBodyParameter("status", String.valueOf(newStatus))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            JSONObject obj = response;
                            String message = obj.getString("message");
                            String userFounded = "Saved";
                            //if no error in response
                            if (message.toLowerCase().contains(userFounded.toLowerCase())) {
                                if(newStatus == Constants.STATUS_ACCEPTED){
                                    Toast.makeText(context, context.getResources().getString(R.string.request_accepted), Toast.LENGTH_SHORT).show();
                                    acceptDialog.dismiss();
                                    list.get(position).setStatus(newStatus);
                                }else{
                                    Toast.makeText(context, context.getResources().getString(R.string.request_rejected), Toast.LENGTH_SHORT).show();
                                    rejectDialog.dismiss();
                                    list.get(position).setStatus(newStatus);
                                }
                                notifyItemChanged(position);

                            }else{
                                Toast.makeText(context, context.getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            Log.e("apply", e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Log.e("applyError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(context, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("student_id")) {
                                Toast.makeText(context, data.getJSONArray("student_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("job_id")) {
                                Toast.makeText(context, data.getJSONArray("job_id").toString(), Toast.LENGTH_SHORT).show();
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

        public TextView jobPositionTitle, jobSeekerName, date, status;
        public Button accept, reject;
        public LinearLayout layoutBtns;

        public ViewHolder(View itemView) {
            super(itemView);
            this.jobPositionTitle = itemView.findViewById(R.id.job_position_title);
            this.jobSeekerName = itemView.findViewById(R.id.jobseeker_name);
            this.date = itemView.findViewById(R.id.date);
            this.accept = itemView.findViewById(R.id.accept);
            this.reject = itemView.findViewById(R.id.reject);
            this.status = itemView.findViewById(R.id.status);
            this.layoutBtns = itemView.findViewById(R.id.layout_btns);
        }
    }


}
