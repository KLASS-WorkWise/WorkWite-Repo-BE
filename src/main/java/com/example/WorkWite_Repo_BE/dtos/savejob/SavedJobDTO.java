package com.example.WorkWite_Repo_BE.dtos.savejob;


import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedJobDTO {
    private Long savedJobId;
    private JobPostingResponseDTO JobPostingResponseDTO;

    private String  savedAt;

}

