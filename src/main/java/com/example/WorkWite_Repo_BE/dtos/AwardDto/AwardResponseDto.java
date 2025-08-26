package com.example.WorkWite_Repo_BE.dtos.AwardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwardResponseDto {
    private Long resumeId;
    private String awardName;
    private LocalDate awardYear;
    private String donViTrao;
    private String description;

}
