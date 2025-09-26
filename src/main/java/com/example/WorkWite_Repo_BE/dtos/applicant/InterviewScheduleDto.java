package com.example.WorkWite_Repo_BE.dtos.applicant;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewScheduleRequest {
    private LocalDateTime interviewDate;
    private String location;
    private String note;
}

