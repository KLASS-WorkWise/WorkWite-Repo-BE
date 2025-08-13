package com.example.WorkWite_Repo_BE.entities;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Data
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employers employer;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "min_experience")
    private Integer minExperience;

    @Column(name = "required_degree", length = 255)
    private String requiredDegree;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "status")
    private String status;
}