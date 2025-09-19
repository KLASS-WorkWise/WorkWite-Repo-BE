package com.example.WorkWite_Repo_BE.dtos.Stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
public class OverviewStatsDto {
    private long totalUsers;
    private long totalEmployers;
    private long totalCandidates;
    private long totalJobPostings;
    private long totalApplications;
}
