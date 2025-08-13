package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto;
import com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto;
import com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatResumeRequestDto {
    private Long candidateId; // ID của Candidate
    private String fullName;
    private String email;
    private String phone;
    private String profilePicture;
    private String summary;
    private String jobTitle;

    private List<CreatEducationRequestDto> educations; // Dữ liệu tạo mới Education
    private List<CreatAwardRequestDto> awards; // Dữ liệu tạo mới Award
    private List<CreatAvtivityRequestDto> activities;
    private List<CreatExperienceRequestDto> experiences;
}
