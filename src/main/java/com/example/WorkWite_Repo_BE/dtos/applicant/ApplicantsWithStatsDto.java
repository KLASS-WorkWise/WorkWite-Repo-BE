package com.example.WorkWite_Repo_BE.dtos.applicant;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicantsWithStatsDto {
    private List<ApplicantResponseDto> applicants;  // danh sách ứng viên
    private Map<String, Long> stats;                // thống kê theo trạng thái
}