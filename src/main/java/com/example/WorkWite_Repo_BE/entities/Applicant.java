package com.example.WorkWite_Repo_BE.entities;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resumeLink;
    private String coverLetter;
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    private LocalDateTime interviewTime;

    @ManyToOne
    @JoinColumn(name = "jobposting_id")
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;


}
