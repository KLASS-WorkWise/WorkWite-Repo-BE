package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.JobScore.RecommendationResponseDto;
import com.example.WorkWite_Repo_BE.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    /**
     * API đề xuất công việc cho 1 ứng viên
     *
     * @param candidateId id ứng viên
     * @param limit số lượng job muốn lấy (default = 10)
     * @param minScore điểm tối thiểu (default = 50)
     */
    @GetMapping("/{candidateId}")
    public ResponseEntity<RecommendationResponseDto> recommendJobs(
            @PathVariable Long candidateId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "50") double minScore
    ) {
        RecommendationResponseDto response =
                recommendationService.recommendForCandidate(candidateId, limit, minScore);
        return ResponseEntity.ok(response);
    }
}
