package com.example.WorkWite_Repo_BE.dtos.ExperienceDto;

import lombok.Data;

@Data
public class ExperienceResponseDto {
    private String companyName;
    private String position;
    private int startYear;
    private int endYear;
    private String description;

    public ExperienceResponseDto(String companyName, String position, int startYear, int endYear, String description) {
        this.companyName = companyName;
        this.position = position;
        this.startYear = startYear;
        this.endYear = endYear;
        this.description = description;
    }
}
