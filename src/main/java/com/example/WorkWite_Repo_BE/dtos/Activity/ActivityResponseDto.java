package com.example.WorkWite_Repo_BE.dtos.Activity;

import lombok.Data;

@Data
public class ActivityResponseDto {
    private String activityName;
    private String organization;
    private Integer startYear;
    private Integer endYear;
    private String description;

    public ActivityResponseDto(String activityName, String organization, Integer startYear, Integer endYear, String description) {
        this.activityName = activityName;
        this.organization = organization;
        this.startYear = startYear;
        this.endYear = endYear;
        this.description = description;
    }
}
