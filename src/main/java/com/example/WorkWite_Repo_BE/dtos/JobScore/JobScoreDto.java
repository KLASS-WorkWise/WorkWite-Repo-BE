package com.example.WorkWite_Repo_BE.dtos.JobScore;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobScoreDto {
    private Long jobPostingId;
    private String jobTitle;
    private String salary_range;
    private String location;
    private String logoUrl;
    private String description;
    private String companyName;
    private String major;
    private double score; // 0.0 - 100.0
    private int matchedSkills;
    private int totalRequiredSkills;
    private double skillMatchPercent;
    private double expScore;
    private double degreeScore;
}
