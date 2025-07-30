package com.example.WorkWite_Repo_BE.dtos.CandidateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCandidateRequestDto {
    private String phone;
    private String address;
    private String bio;
    private String experience;
    private String skills;
}
