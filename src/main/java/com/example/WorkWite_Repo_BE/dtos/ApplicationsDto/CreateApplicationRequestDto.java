package com.example.WorkWite_Repo_BE.dtos.ApplicationsDto;

import com.example.WorkWite_Repo_BE.entities.Candidates;
import com.example.WorkWite_Repo_BE.entities.JobPosting;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequestDto {

    @NotNull(message = "JobPosting is required")
    private JobPosting jobPosting;

    @NotNull(message = "Candidate is required")
    private Candidates candidate;

    @NotNull(message = "coverLetter is required")
    private String coverLetter;
    private String status;
    private LocalDateTime appliedAt;

}
