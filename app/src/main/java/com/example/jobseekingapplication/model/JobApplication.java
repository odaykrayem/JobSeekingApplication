package com.example.jobseekingapplication.model;

public class JobApplication {
    private int id;
    private String jobTitle;
    private String companyName;
    private int status;
    private String date;

    public JobApplication(int id, String jobTitle, String companyName, int status, String date) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
