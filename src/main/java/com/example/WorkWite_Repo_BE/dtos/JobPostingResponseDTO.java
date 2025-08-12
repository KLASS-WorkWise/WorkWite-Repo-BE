package com.example.WorkWite_Repo_BE.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobPostingResponseDTO {
    private Long id;
    private Long employerId;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private String jobType;
    private String category;
    private String requiredSkills;
    private Integer minExperience;
    private String requiredDegree;
    private LocalDateTime endAt;
    private String status;
    private LocalDateTime createdAt;
}