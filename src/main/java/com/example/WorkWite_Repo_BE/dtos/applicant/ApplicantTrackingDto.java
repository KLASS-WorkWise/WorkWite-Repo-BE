package com.example.WorkWite_Repo_BE.dtos.applicant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ApplicantTrackingDto.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantTrackingDto {
    private ApplicantResponseDto detail;              // thông tin đơn apply
    private List<ApplicantHistoryDto> history;        // lịch sử thay đổi
    private List<TimelineEventResponse> timeline;      // tiến trình apply
}
