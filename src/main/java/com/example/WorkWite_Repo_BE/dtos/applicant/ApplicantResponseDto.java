package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantResponseDto {
    private Long id;
    private Long jobId;
    private Long candidateId;
    private Long resumesId;

    private String resumeLink;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;
    private List<String> missingSkills; // danh sách skill thiếu
    private String minExperience; // cảnh báo kinh nghiệm nếu chưa đủ
    private Integer experienceYears; // ✅ thêm số năm
    private Double skillMatchPercent;      // ✅ thêm
    private Boolean isSkillQualified;      // ✅ thêm
    private Boolean isExperienceQualified; // ✅ thêm
    // Thêm history
    private List<ApplicantHistoryDto> history;

}
