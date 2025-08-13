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
    private User user;
    private List<SaveJobResponseDto> savedJobs;
    private List<ResumeResponseDto> resumes;
    //còn aplly chua update đc

    public CandidatesResponseDto(Long id, User user, List<SaveJobResponseDto> savedJobs, List<ResumeResponseDto> resumes) {
        this.id = id;
        this.user = user;
        this.savedJobs = savedJobs;
        this.resumes = resumes;
    }

}
