package com.example.WorkWite_Repo_BE.dtos.ApplicationsDto;

import com.example.WorkWite_Repo_BE.entities.Candidates;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppRequestDto {
    @NotNull(message = "JobPosting is required")
    private JobPosting jobPosting;
    private Candidates candidate;
    private String coverLetter;
    private String status;
    private LocalDateTime appliedAt;
}
