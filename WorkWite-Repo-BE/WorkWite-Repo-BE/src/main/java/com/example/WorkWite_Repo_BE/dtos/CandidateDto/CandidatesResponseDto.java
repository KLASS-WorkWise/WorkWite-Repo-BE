package com.example.WorkWite_Repo_BE.dtos.CandidateDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidatesResponseDto {
    private Long id;
    private String phone;
    private String address;
    private String bio;
    private String experience;
    private String education;
    private String skills;

    public CandidatesResponseDto(Long id, String phone, String address, String bio, String experience, String education, String skills) {
        this.id = id;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
        this.experience = experience;
        this.education = education;
        this.skills = skills;

    }
}
