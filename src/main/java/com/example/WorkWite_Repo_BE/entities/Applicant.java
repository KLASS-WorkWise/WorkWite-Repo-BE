package com.example.WorkWite_Repo_BE.entities;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "job_id"}))
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

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "min_experience")
    private String minExperience;

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



