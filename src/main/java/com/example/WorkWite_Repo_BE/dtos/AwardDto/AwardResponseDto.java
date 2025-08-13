package com.example.WorkWite_Repo_BE.dtos.AwardDto;

import lombok.Data;

@Data
public class AwardResponseDto {
    private Long resumeId;
    private String awardName;
    private Integer awardYear;

    public AwardResponseDto(Long resumeId, String schoolName, Integer degree) {
        this.resumeId = resumeId;
        this.awardName = schoolName;
        this.awardYear = degree;
    }
}
