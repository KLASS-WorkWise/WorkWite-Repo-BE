package com.example.WorkWite_Repo_BE.dtos.Education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEducationRequestDto {
    private String schoolName;
    private String degree;
    private String major;
    private Integer startYear;
    private Integer endYear;
    private String GPA;
}
