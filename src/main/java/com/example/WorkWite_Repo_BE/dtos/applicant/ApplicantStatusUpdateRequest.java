package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantStatusUpdateRequest {
    @JsonAlias({"status", "newStatus"})
    private ApplicationStatus status;  // INTERVIEW, OFFER, HIRED, REJECTED
    private String note;               // Ghi chú (VD: lịch phỏng vấn, lý do reject…)


    // chỉ cần khi status = INTERVIEW
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime scheduledAt;
    private String location;
    private String interviewer;
}
