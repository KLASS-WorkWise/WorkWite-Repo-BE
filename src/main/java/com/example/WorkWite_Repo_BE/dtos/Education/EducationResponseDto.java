package com.example.WorkWite_Repo_BE.dtos.Education;

import lombok.Data;

@Data

public class EducationResponseDto {
    private Long resumeId;
    private String schoolName;
    private String degree;
    private String major;
    private Integer startYear;
    private Integer endYear;
    private String GPA;

    public EducationResponseDto(Long resumeId, String schoolName, String degree, String major, Integer startYear, Integer endYear, String GPA) {
        this.resumeId = resumeId;
        this.schoolName = schoolName;
        this.degree = degree;
        this.major = major;
        this.startYear = startYear;
        this.endYear = endYear;
        this.GPA = GPA;
    }


}
