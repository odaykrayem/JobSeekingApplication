package com.mrmindteam.jobseekingapplication.model;

public class JobVacancy {
    private int id;
    private String jobPositionTitle;
    private String requiredSkills;
    private String requiredExperience;
    private String workType;
    private String workTime;
    private String salaryRange;

    public JobVacancy(int id, String jobPositionTitle, String requiredSkills, String requiredExperience, String workType, String workTime, String salaryRange) {
        this.id = id;
        this.jobPositionTitle = jobPositionTitle;
        this.requiredSkills = requiredSkills;
        this.requiredExperience = requiredExperience;
        this.workType = workType;
        this.workTime = workTime;
        this.salaryRange = salaryRange;
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
