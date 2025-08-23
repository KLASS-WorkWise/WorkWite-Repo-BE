package com.example.WorkWite_Repo_BE.dtos.ExperienceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExperienceRequestDto {
    private String companyName;
    private String position;
    private LocalDate startYear;
    private LocalDate endYear;
    private String description;
}
