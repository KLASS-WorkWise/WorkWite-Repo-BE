package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import lombok.Builder;
import lombok.Data;

@Data

public class ResumeResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String profilePicture;
    private String summary;
    private String createdAt;

    public ResumeResponseDto(Long id, String fullName, String email, String phone, String profilePicture, String summary, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
        this.summary = summary;
        this.createdAt = createdAt;
    }
}
