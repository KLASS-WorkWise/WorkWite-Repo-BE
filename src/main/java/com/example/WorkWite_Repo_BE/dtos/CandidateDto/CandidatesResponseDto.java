package com.example.WorkWite_Repo_BE.dtos.CandidateDto;

import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.AppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SavedJobDto.SaveJobResponseDto;
import com.example.WorkWite_Repo_BE.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class CandidatesResponseDto {
    private Long id;

    // feil của user
    private String username;
    private String email;
    // candidate
    private String phoneNumber;
    private String name;
    private String avatar;
    private List<SaveJobResponseDto> savedJobs;
    private List<ResumeResponseDto> resumes;
    //còn aplly chua update đc

    public CandidatesResponseDto(Long id, String username,String email,
                                 String phoneNumber,String name, String avatar,List<SaveJobResponseDto> savedJobs, List<ResumeResponseDto> resumes) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.avatar = avatar;
        this.savedJobs = savedJobs;
        this.resumes = resumes;
    }

   }
