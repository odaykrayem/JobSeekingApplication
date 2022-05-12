package com.example.jobseekingapplication.model;

import com.example.jobseekingapplication.utils.Constants;
import com.example.jobseekingapplication.utils.Urls;

import java.io.Serializable;

public class JobSeeker implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String education;
    private String workExperience;
    private String language;
    private String skills;
    private int skillId;
    private String summary;
    private String cv;

    public JobSeeker(int id, String name, String email, String phone, String education, String workExperience, String language, String skills, int skillId, String summary, String cv) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.education = education;
        this.workExperience = workExperience;
        this.language = language;
        this.skills = skills;
        this.skillId = skillId;
        this.summary = summary;
        this.cv = cv;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getEducation() {
        return education;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public String getLanguage() {
        return language;
    }

    public String getSkills() {
        return skills;
    }

    public String getSummary() {
        return summary;
    }

    public String getCv() {
        return cv;
    }
}
