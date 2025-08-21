package com.example.WorkWite_Repo_BE.dtos.ExperienceDto;

import lombok.Data;

@Data
public class ExperienceResponseDto {
    private Long id;
    private String companyName;
    private String position;
    private Integer startYear;
    private Integer endYear;
    private String description;

    public ExperienceResponseDto(Long id, String companyName, String position, Integer startYear, Integer endYear, String description) {
        this.id = id;
        this.companyName = companyName;
        this.position = position;
        this.startYear = startYear;
        this.endYear = endYear;
        this.description = description;
    }
}

