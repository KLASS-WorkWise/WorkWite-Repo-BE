package com.example.WorkWite_Repo_BE.dtos.ExperienceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExperienceRequestDto {
    private String companyName;
    private String position;
    private int startYear;
    private int endYear;
    private String description;
}
