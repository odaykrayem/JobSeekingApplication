package com.example.jobseekingapplication.jobs.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.jobseeker.adapters.SkillsAdapter;
import com.example.jobseekingapplication.model.JobVacancy;
import com.example.jobseekingapplication.model.Skill;
import com.example.jobseekingapplication.utils.Urls;
import com.example.jobseekingapplication.utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateJobVacancyFragment extends Fragment {

    EditText mJobPositionTitleET, mRequiredExperienceET, mWorkTypeET, mWorkTimeET, mSalaryRangeET;
    String jobPositionTitle, requiredExperience, workType, workTime, salaryRange;
    Button mUpdateJobBtn;
    AppCompatSpinner mSkillsSpinner;
    ArrayList<Skill> skillsList;
    int selectedSkillId = -1;

    Context context;
    NavController navController;
    ProgressDialog pDialog;

    JobVacancy jobVacancy;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public UpdateJobVacancyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobVacancy = (JobVacancy) getArguments().getSerializable("job");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_job_vacancy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mJobPositionTitleET = view.findViewById(R.id.job_position_title);
        mRequiredExperienceET = view.findViewById(R.id.required_experience);
        mWorkTypeET = view.findViewById(R.id.work_type);
        mWorkTimeET = view.findViewById(R.id.work_time);
        mSalaryRangeET = view.findViewById(R.id.salary_range);
        mUpdateJobBtn = view.findViewById(R.id.update_job);
        mSkillsSpinner = view.findViewById(R.id.skills_chooser);
        navController = Navigation.findNavController(view);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        mJobPositionTitleET.setText(jobVacancy.getJobPositionTitle());
        mRequiredExperienceET.setText(jobVacancy.getRequiredExperience());
        mWorkTypeET.setText(jobVacancy.getWorkType());
        mWorkTimeET.setText(jobVacancy.getWorkTime());
        mSalaryRangeET.setText(jobVacancy.getSalaryRange());
        selectedSkillId = jobVacancy.getSkillId();

        getSkills();


        mUpdateJobBtn.setOnClickListener(v -> {
            if (Validation.validateInput(context, mJobPositionTitleET, mRequiredExperienceET, mWorkTypeET, mWorkTimeET, mSalaryRangeET)) {
                if (selectedSkillId == -1) {
                    Toast.makeText(context, getResources().getString(R.string.please_choose_skill), Toast.LENGTH_SHORT).show();

                } else {
                    updateJob();
                }
            }
        });

    }

    private void updateJob() {
        pDialog.show();
        mUpdateJobBtn.setEnabled(false);

        String url = Urls.UPDATE_JOB;

        jobPositionTitle = mJobPositionTitleET.getText().toString().trim();
        workType = mWorkTypeET.getText().toString().trim();
        workTime = mWorkTimeET.getText().toString().trim();
        requiredExperience = mRequiredExperienceET.getText().toString().trim();
        salaryRange = mSalaryRangeET.getText().toString().trim();

        AndroidNetworking.post(url)
                .addBodyParameter("job_id", String.valueOf(jobVacancy.getId()))
                .addBodyParameter("job_position_title", jobPositionTitle)
                .addBodyParameter("skill_id", String.valueOf(selectedSkillId))
                .addBodyParameter("required_experience", requiredExperience)
                .addBodyParameter("workType", workType)
                .addBodyParameter("workTime", workTime)
                .addBodyParameter("salaryRange", salaryRange)
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
                                Toast.makeText(context, context.getResources().getString(R.string.update_job_success), Toast.LENGTH_SHORT).show();
                                navController.popBackStack();
                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                            }
                            mUpdateJobBtn.setEnabled(true);
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mUpdateJobBtn.setEnabled(true);
                            pDialog.dismiss();
                            Log.e("addjob catch", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mUpdateJobBtn.setEnabled(true);
                        Log.e("addjobError", anError.getErrorBody());
                        try {
                            JSONObject error = new JSONObject(anError.getErrorBody());
                            JSONObject data = error.getJSONObject("data");
                            Toast.makeText(context, error.getString("message"), Toast.LENGTH_SHORT).show();
                            if (data.has("company_id")) {
                                Toast.makeText(context, data.getJSONArray("company_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("job_position_title")) {
                                Toast.makeText(context, data.getJSONArray("job_position_title").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("skill_id")) {
                                Toast.makeText(context, data.getJSONArray("skill_id").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("required_experience")) {
                                Toast.makeText(context, data.getJSONArray("required_experience").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("workType")) {
                                Toast.makeText(context, data.getJSONArray("workType").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("workTime")) {
                                Toast.makeText(context, data.getJSONArray("workTime").toString(), Toast.LENGTH_SHORT).show();
                            }
                            if (data.has("salaryRange")) {
                                Toast.makeText(context, data.getJSONArray("salaryRange").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getSkills() {
        String url = Urls.GET_SKILLS;
        skillsList = new ArrayList<Skill>();
        pDialog.show();

        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            String message = jsonObject.getString("message");
                            String successMessage = "Found";
                            int selectedSk = -1;
                            if (message.toLowerCase().contains(successMessage.toLowerCase())) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if(Integer.parseInt(obj.getString("id")) == jobVacancy.getSkillId()){
                                        selectedSk = i;
                                    }
                                    skillsList.add(
                                            new Skill(
                                                    Integer.parseInt(obj.getString("id")),
                                                    obj.getString("skill")
                                            )
                                    );
                                }
                                SkillsAdapter skillsAdapter = new SkillsAdapter(context, R.layout.support_simple_spinner_dropdown_item, skillsList);
                                mSkillsSpinner.setAdapter(skillsAdapter);
                                mSkillsSpinner.setSelection(selectedSk);
                                mSkillsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        selectedSkillId = skillsList.get(position).getId();
                                        Log.e("selected Skill", skillsList.get(position).getSkill());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                Toast.makeText(context, getResources().getString(R.string.data_loaded), Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            Log.e("jobs catch", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        Log.e("jobs anerror", error.getErrorBody());
                    }
                });
    }

}