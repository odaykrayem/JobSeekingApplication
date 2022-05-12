package com.example.jobseekingapplication.jobseeker.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.model.JobApplication;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import com.example.jobseekingapplication.utils.Urls;

import java.util.ArrayList;
import java.util.List;

public class AppliedJobsAdapter extends RecyclerView.Adapter<AppliedJobsAdapter.ViewHolder> {

    Context context;
    private List<JobApplication> list;

    public AppliedJobsAdapter(Context context, ArrayList<JobApplication> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_job_application, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JobApplication item = list.get(position);

        holder.jobPositionTitle.setText(item.getJobTitle());
        holder.companyName.setText(item.getCompanyName());
        holder.date.setText(item.getDate());
        if(item.getStatus() == Constants.STATUS_PROCESSING){
            holder.status.setText(Constants.STATUS_PROCESSING_TXT);
            holder.status.setBackground(context.getDrawable(R.drawable.bg_status_processing));
        }
        else if(item.getStatus() == Constants.STATUS_ACCEPTED){
            holder.status.setText(Constants.STATUS_ACCEPTED_TXT);
            holder.status.setBackground(context.getDrawable(R.drawable.bg_status_accepted));
        }
        else{
            holder.status.setText(Constants.STATUS_REJECTED_TXT);
            holder.status.setBackground(context.getDrawable(R.drawable.bg_status_rejected));
        }
        
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView jobPositionTitle, companyName, date, status;

        public ViewHolder(View itemView) {
            super(itemView);
            this.jobPositionTitle = itemView.findViewById(R.id.job_position_title);
            this.companyName = itemView.findViewById(R.id.company);
            this.date = itemView.findViewById(R.id.date);
            this.status = itemView.findViewById(R.id.status);
        }
    }


}
