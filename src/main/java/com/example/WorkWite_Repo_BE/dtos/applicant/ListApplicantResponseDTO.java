package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListApplicantResponseDTO {
    private Long jobPostingId;
    private String jobTitle;

    private Long candidateId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatar;

    private String coverLetter;
    private String resumeLink;

    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;

    /**
     * Map từ entity Applicant sang DTO
     */
    public static ListApplicantResponseDTO fromEntity(Applicant applicant) {
        return ListApplicantResponseDTO.builder()
                .jobPostingId(applicant.getJobPosting().getId())
                .jobTitle(applicant.getJobPosting().getTitle())

                .candidateId(applicant.getCandidate().getId())
                .fullName(applicant.getCandidate().getUser().getFullName())
                .email(applicant.getCandidate().getUser().getEmail())
                .phoneNumber(applicant.getCandidate().getPhoneNumber())
                .avatar(applicant.getCandidate().getAvatar())

                .coverLetter(applicant.getCoverLetter())
                .resumeLink(applicant.getResumeLink())

                .applicationStatus(applicant.getApplicationStatus())
                .appliedAt(applicant.getAppliedAt())
                .build();
    }
}
