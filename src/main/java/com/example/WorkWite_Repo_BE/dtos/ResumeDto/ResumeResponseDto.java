package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import com.example.WorkWite_Repo_BE.entities.*;
import lombok.Data;

import java.util.List;

@Data

public class    ResumeResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String profilePicture;
    private String summary;
    private String createdAt;
    private String jobTitle;
    private List<Education> educations;
    private List<Award> awards;
    private List<Activity> activities;
    private List<Application> applications;
    private List<Skill> skills;

    // Constructor với tất cả các trường


    public ResumeResponseDto(Long id, String profilePicture, String fullName, String email, String phone,
                             String createdAt, String jobTitle, List<Activity> activities,
                             List<Education> educations, List<Award> awards, List<Application> applications,
                             List<Skill> skills,String summary) {
        this.id = id;
        this.profilePicture = profilePicture;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.jobTitle = jobTitle;
        this.activities = activities;
        this.educations = educations;
        this.awards = awards;
        this.applications = applications;
        this.skills = skills;
        this.summary = summary;
    }
}
