package com.example.WorkWite_Repo_BE.dtos.applicant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantHistoryDto {
    private String step;
    private String status;
    private LocalDateTime date;
}
