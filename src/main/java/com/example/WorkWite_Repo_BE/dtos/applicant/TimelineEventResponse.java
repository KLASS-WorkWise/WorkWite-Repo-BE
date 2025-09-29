package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimelineEventResponse {
    private int stepOrder;                          // Thứ tự step
    private ApplicationStatus status;               // PENDING, INTERVIEW, OFFER...
    private List<Object> events;       // Các sự kiện trong step
    private boolean currentStep;                    // Step hiện tại
    private boolean completed;                      // Step đã hoàn thành
}
