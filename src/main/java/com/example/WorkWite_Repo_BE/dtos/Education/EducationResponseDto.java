
package com.example.WorkWite_Repo_BE.dtos.Education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class EducationResponseDto {
    private Long resumeId;
    private String schoolName;
    private String degree;
    private String major;
    private LocalDate startYear;
    private LocalDate endYear;
    private String GPA;


}
