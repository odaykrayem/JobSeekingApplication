package com.example.jobseekingapplication.jobseeker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.OnApplyBtnClicked;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class JobVacanciesAdapter extends RecyclerView.Adapter<JobVacanciesAdapter.ViewHolder> implements Filterable {

    Context context;
    private List<JobVacancy> list;
    private List<JobVacancy> filteredList;
    public NavController navController;
    OnApplyBtnClicked onApplyBtnClickedInstance;

    public JobVacanciesAdapter(Context context, ArrayList<JobVacancy> list, OnApplyBtnClicked listener) {
        this.context = context;
        this.list = list;
        this.filteredList = list;
        this.onApplyBtnClickedInstance = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_job_vacancy, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JobVacancy item = list.get(position);

        holder.jobPositionTitle.setText(item.getJobPositionTitle());
        holder.companyName.setText(item.getCompanyName());
        holder.workType.setText(item.getWorkType());

        holder.apply.setOnClickListener(v->{
            LayoutInflater factory = LayoutInflater.from(context);
            final View view1 = factory.inflate(R.layout.dialog_confirm_job_application, null);
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            dialog.setView(view1);
            dialog.setCanceledOnTouchOutside(true);

            TextView yes = view1.findViewById(R.id.yes_btn);
            TextView no = view1.findViewById(R.id.no_btn);
            yes.setOnClickListener(l->{
                //Interface is in JobsOpportunities Fragment
                onApplyBtnClickedInstance.onApplyBtnClicked(item.getId());
                dialog.dismiss();
            });

            no.setOnClickListener(l->{
                dialog.dismiss();
            });
            dialog.show();

        });

        holder.itemView.setOnClickListener(v->{
            navController = Navigation.findNavController(holder.itemView);
            Bundle bundle = new Bundle();

            bundle.putSerializable(Constants.KEY_JOB_VACANCY, item);
            navController.navigate(R.id.action_jobVacanciesToJobDetails,bundle);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String request = charSequence.toString().split(":")[1];
                String searchString = charSequence.toString().split(":")[0];
                List<JobVacancy> resultData = new ArrayList<>();

                if(request.equals(Constants.SEARCH)){
                    if(searchString == null | searchString.length() == 0){
                        filterResults.count = filteredList.size();
                        filterResults.values = filteredList;

                    }else{
                        for(JobVacancy jobVacancy: list){
                            if(jobVacancy.getJobPositionTitle().toLowerCase().contains(searchString)){
                                resultData.add(jobVacancy);
                            }
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }else{
//
                    int userSkillId = SharedPrefManager.getInstance(context).getJobSeekerData().getSkillId();
                    for(JobVacancy jobVacancy: filteredList){
                        if(jobVacancy.getSkillId() == userSkillId){
                            resultData.add(jobVacancy);

                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;
                }

                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (List<JobVacancy>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView jobPositionTitle, companyName, workType;
        public Button apply;

        public ViewHolder(View itemView) {
            super(itemView);
            this.jobPositionTitle = itemView.findViewById(R.id.job_position_title);
            this.companyName = itemView.findViewById(R.id.company_name);
            this.workType = itemView.findViewById(R.id.work_type);
            this.apply = itemView.findViewById(R.id.btn_apply);
        }
    }


}
