package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "applicant_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column(nullable = false)
    private String step; // "Applied", "HR Screening", "Interview" ...

    @Column(nullable = false)
    private String status; // "pending", "in-progress", "done"

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
