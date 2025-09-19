package com.example.WorkWite_Repo_BE.entities;

import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicant_history")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(length = 500)
    private String note;

    private LocalDateTime changedAt = LocalDateTime.now();;

    private String changedBy; // HR/Admin/Candidate
}
