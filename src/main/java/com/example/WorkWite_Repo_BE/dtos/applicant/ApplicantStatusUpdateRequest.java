package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantStatusUpdateRequest {
    private ApplicationStatus status;  // INTERVIEW, OFFER, HIRED, REJECTED
    private String note;               // Ghi chú (VD: lịch phỏng vấn, lý do reject…)
}
