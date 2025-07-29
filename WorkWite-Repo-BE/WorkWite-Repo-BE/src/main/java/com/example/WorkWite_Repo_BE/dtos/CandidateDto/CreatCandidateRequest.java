package com.example.WorkWite_Repo_BE.dtos.CandidateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatCandidateRequest {
    private String phone;
    private String address;
    private String bio;
    private String experience;
    private String education;
    private String skills;

}
