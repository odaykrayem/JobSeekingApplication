package com.example.jobseekingapplication.model;

public class Skill {
    private int id;
    private String skill;

    public Skill(int id, String skill) {
        this.id = id;
        this.skill = skill;
    }

    public int getId() {
        return id;
    }

    public String getSkill() {
        return skill;
    }
}
