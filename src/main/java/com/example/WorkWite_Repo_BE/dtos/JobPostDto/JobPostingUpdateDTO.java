package com.example.WorkWite_Repo_BE.dtos.JobPostDto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class JobPostingUpdateDTO {
    private Long employerId;

    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String description;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 100, message = "Salary range must not exceed 100 characters")
    private String salaryRange;

    private String jobType;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private List<String> requiredSkills;

    private Integer minExperience;

    private String requiredDegree;

    private java.time.LocalDateTime endAt;

    private String status;
}
