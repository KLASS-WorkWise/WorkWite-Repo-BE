package com.example.WorkWite_Repo_BE.dtos.JobPostDto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobPostingResponseDTO {
    private Long id;
    private Long employerId;
    private String employerName;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private String jobType;
    private String category;
    private List<String> requiredSkills;
    private Integer minExperience;
    private String requiredDegree;
    private LocalDateTime endAt;
    private String status;
    private LocalDateTime createdAt;
}