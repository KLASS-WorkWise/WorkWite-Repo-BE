package com.example.WorkWite_Repo_BE.entities;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
//@EntityListeners(ApplicantEntityListener.class)
@Table( name = "applicants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"job_posting_id", "candidate_id"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "resume_link")
    private String resumeLink;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;
    private LocalDateTime interviewTime;


    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 20)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "applicant_missing_skills", joinColumns = @JoinColumn(name = "applicant_id"))
    @Column(name = "skill")
    private List<String> missingSkills = new ArrayList<>();

    @Column(name = "min_experience")
    private String minExperience;
    @Column(name = "experience_years")
    private Integer experienceYears;   // số năm kinh nghiệm

    @Column(name = "skill_match_percent")
    private Double skillMatchPercent;  // % match kỹ năng

    @Column(name = "is_skill_qualified")
    private Boolean isSkillQualified;  // có đạt yêu cầu skill không

    @Column(name = "is_experience_qualified")
    private Boolean isExperienceQualified; // có đạt yêu cầu exp không

    @Column(length = 500)
    private String skillMatchMessage;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;  // mặc định false khi tạo

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApplicantHistory> history;
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterviewSchedule> schedules;

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
        if (applicationStatus == null) {
            applicationStatus = ApplicationStatus.PENDING;
        }

    }


}



