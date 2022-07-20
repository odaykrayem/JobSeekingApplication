package com.example.jobseekingapplication.model;

public class Jobs {

    private int id;
    private String companyName;
    private String companyAddress;
    private String companyDetails;
    private String email;

    public Jobs(int id, String companyName, String companyAddress, String companyDetails, String email) {
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
