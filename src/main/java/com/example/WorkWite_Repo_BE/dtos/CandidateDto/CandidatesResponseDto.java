package com.example.WorkWite_Repo_BE.dtos.CandidateDto;


import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.savejob.SavedJobDTO;
import com.example.WorkWite_Repo_BE.entities.User;
import lombok.Data;

import java.util.List;

@Data
public class CandidatesResponseDto {
    private Long id;
    private User user;
    private String phoneNumber;
    private String avatar;
    private List<SavedJobDTO> savedJobs;
    private List<ResumeResponseDto> resumes;


    public CandidatesResponseDto(Long id, User user, String phoneNumber, String avatar, List<SavedJobDTO> savedJobs, List<ResumeResponseDto> resumes) {
        this.id = id;
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.savedJobs = savedJobs;
        this.resumes = resumes;

    }

}
