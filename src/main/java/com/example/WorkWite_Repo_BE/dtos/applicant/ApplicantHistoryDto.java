package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantHistoryDto {
    private Long id;
    private ApplicationStatus status;
    private String note;
    private LocalDateTime changedAt;
    private String changedBy;
}
