package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdataResumeRequestDto {
    private String fullName;
    private String email;
    private String phone;
    private String profilePicture;
    private String summary;
    private String jobTitle;
}
