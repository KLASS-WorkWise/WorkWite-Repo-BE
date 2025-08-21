package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantRequestDto {

    private Long resumesId;              // optional
    private MultipartFile resumeFile;    // optional
    private String resumeLink;           // optional
    private String coverLetter;          // optional



}
