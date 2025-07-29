package com.example.WorkWite_Repo_BE.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class JobPostingRequestDTO {
    @NotNull(message = "Employer ID is required")
    private Long employerId;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String description;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 100, message = "Salary range must not exceed 100 characters")
    private String salaryRange;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private LocalDate deadline;

    @NotBlank(message = "Status is required")
    private String status;
}