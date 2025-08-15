package com.example.WorkWite_Repo_BE.dtos.AwardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAwardRequestDto {
    private String awardName;
    private Integer awardYear;
}
