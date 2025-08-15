package com.example.WorkWite_Repo_BE.dtos.applicant;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class AppResponseDto {
    private Integer id;
    private Integer jobPostingId;
    private Integer candidateId;

    private String coverLetter;

    private String status;

    private LocalDateTime appliedAt;




    public AppResponseDto(Integer id,Integer jobPostingId, Integer candidateId, String coverLetter, String status, LocalDateTime appliedAt) {
        this.id = id;
        this.jobPostingId = jobPostingId;
        this.candidateId = candidateId;
        this.coverLetter = coverLetter;
        this.status = status;
        this.appliedAt = appliedAt;

    }
}
