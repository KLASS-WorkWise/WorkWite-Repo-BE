package com.example.WorkWite_Repo_BE.dtos.Activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatAvtivityRequestDto {

    private String activityName;
    private String organization;
    private LocalDate startYear;
    private LocalDate endYear;
    private String description;
}
