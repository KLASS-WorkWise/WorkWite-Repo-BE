package com.example.WorkWite_Repo_BE.dtos.Activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatAvtivityRequestDto {

    private String activityName;
    private String role;
    private Integer startYear;
    private Integer endYear;
}
