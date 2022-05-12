package com.example.jobseekingapplication.model;

public class JobRequest {
    private int id;
    private String jobTitle;
    private String jobSeekerName;
    private int status;
    private String date;
    private JobSeeker jobSeeker;
    public JobRequest(int id, String jobTitle, String jobSeekerName, int status, String date) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobSeekerName = jobSeekerName;
        this.status = status;
        this.date = date;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    public int getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobSeekerName() {
        return jobSeekerName;
    }

    public int getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobSeekerName(String jobSeekerName) {
        this.jobSeekerName = jobSeekerName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
