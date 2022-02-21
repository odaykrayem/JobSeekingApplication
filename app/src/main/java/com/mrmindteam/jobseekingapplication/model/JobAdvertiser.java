package com.mrmindteam.jobseekingapplication.model;

public class JobAdvertiser {

    private int id;
    private String companyName;
    private String companyAddress;
    private String companyDetails;
    private String email;

    public JobAdvertiser(int id, String companyName, String companyAddress, String companyDetails, String email) {
        this.id = id;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyDetails = companyDetails;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyDetails() {
        return companyDetails;
    }

    public String getEmail() {
        return email;
    }


}
