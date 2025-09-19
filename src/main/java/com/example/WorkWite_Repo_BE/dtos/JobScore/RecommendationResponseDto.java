package com.example.WorkWite_Repo_BE.dtos.JobScore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDto {
    private Long candidateId;
    private List<JobScoreDto> recommendations;
}
