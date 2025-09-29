// InterviewSchedule.java
package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    private LocalDateTime scheduledAt;
    private String location;
    private String interviewer;

    private boolean reminderSent = false; // để tránh gửi nhắc lại nhiều lần
}
