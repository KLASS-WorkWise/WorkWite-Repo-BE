package com.example.WorkWite_Repo_BE.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore
    private Candidate candidate;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "lob_title")
    private String jobTitle;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // update fetch , fetch = FetchType.EAGER
    @OneToMany(mappedBy = "resume")
    private List<Education> educations;

    @OneToMany(mappedBy = "resume")
    private List<Award> awards;

    @OneToMany(mappedBy = "resume")
    private List<Activity> Activities;

    @OneToMany(mappedBy = "resume")
    private List<Experience> experiences;

    @OneToMany(mappedBy = "resume")
    private List<Applicant> applicants;

    @ElementCollection
    @CollectionTable(
            name = "resumes_skills",
            joinColumns = @JoinColumn(name = "resumes_id")
    )
    private List<String> skillsResumes;
}
