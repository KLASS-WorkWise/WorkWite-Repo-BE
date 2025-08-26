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
    private List<Long> applicantIds;
    private List<String> skillsResumes;
    private Long candidateId;

    // Constructor với tất cả các trường


    public ResumeResponseDto(Long id, String profilePicture, String fullName, String email, String phone,
                             String createdAt, String jobTitle, List<Activity> activities,
                             List<Education> educations, List<Award> awards, List<Long> applicantIds,
                             List<String> skillsResumes, String summary,Long candidateId) {
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
        this.applicantIds = applicantIds;
        this.skillsResumes = skillsResumes;
        this.summary = summary;
        this.candidateId=candidateId;
    }
}
