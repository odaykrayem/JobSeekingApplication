package com.example.jobseekingapplication.model;

import java.io.Serializable;

public class JobVacancy implements Serializable {

    private int id;
    private String jobPositionTitle;
    private String requiredSkills;
    private String requiredExperience;
    private String workType;
    private String workTime;
    private String salaryRange;
    private String companyName;
    private int skillId;

    public JobVacancy(int id,String companyName,String  jobPositionTitle, String requiredSkills, int skillId,String requiredExperience, String workType, String workTime, String salaryRange) {
        this.id = id;
        this.companyName = companyName;
        this.jobPositionTitle = jobPositionTitle;
        this.requiredSkills = requiredSkills;
        this.skillId = skillId;
        this.requiredExperience = requiredExperience;
        this.workType = workType;
        this.workTime = workTime;
        this.salaryRange = salaryRange;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getId() {
        return id;
    }

    public String getJobPositionTitle() {
        return jobPositionTitle;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public String getRequiredExperience() {
        return requiredExperience;
    }

    public String getWorkType() {
        return workType;
    }

    public String getWorkTime() {
        return workTime;
    }

    public String getSalaryRange() {
        return salaryRange;
    }
}
