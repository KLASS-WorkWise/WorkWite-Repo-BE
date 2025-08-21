package com.example.WorkWite_Repo_BE.dtos.AwardDto;

import lombok.Data;

@Data
public class AwardResponseDto {
    private Long resumeId;
    private String awardName;
    private Integer awardYear;
    private String donViTrao;
    private String description;


    public AwardResponseDto(Long resumeId, String awardName, Integer awardYear,String donViTrao, String description) {
        this.resumeId = resumeId;
        this.awardName = awardName;
        this.awardYear = awardYear;
        this.donViTrao = donViTrao;
        this.description = description;

    }
}
