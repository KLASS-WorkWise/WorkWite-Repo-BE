package com.example.WorkWite_Repo_BE.dtos.Activity;

import lombok.Data;

@Data
public class ActivityResponseDto {
    private String activityName;
    private String role;
    private Integer startYear;
    private Integer endYear;

    public ActivityResponseDto(String activityName, String role, Integer startYear, Integer endYear) {
        this.activityName = activityName;
        this.role = role;
        this.startYear = startYear;
        this.endYear = endYear;
    }
}
