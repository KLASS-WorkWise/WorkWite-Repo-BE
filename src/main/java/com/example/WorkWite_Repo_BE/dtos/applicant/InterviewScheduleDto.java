// InterviewScheduleDto.java
package com.example.WorkWite_Repo_BE.dtos.applicant;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InterviewScheduleDto {
    private Long id;
    private LocalDateTime scheduledAt;
    private String location;
    private String interviewer;
}
