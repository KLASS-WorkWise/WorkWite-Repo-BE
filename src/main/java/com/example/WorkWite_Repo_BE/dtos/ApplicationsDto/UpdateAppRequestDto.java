package com.example.WorkWite_Repo_BE.dtos.ApplicationsDto;

import com.example.WorkWite_Repo_BE.entities.Candidate;
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
public class UpdateAppRequestDto {
    @NotNull(message = "JobPosting is required")
    private JobPosting jobPosting;
    private Candidate candidate;
    private String coverLetter;
    private String status;
    private LocalDateTime appliedAt;
}
