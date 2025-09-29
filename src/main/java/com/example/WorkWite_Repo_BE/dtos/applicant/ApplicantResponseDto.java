package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
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
    private String jobTitle;

    private String fullName;
    private String companyName;
    private String logoUrl;
    private String description_company;
    private String location_company;
    private String salaryRange;

    private String description;
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
    private String skillMatchMessage;
    private Boolean isRead = false;


    // Thêm history
    private List<ApplicantHistoryDto> history;

}
