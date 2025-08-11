package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "user_id")
   private User user;

   @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<SavedJob> savedJobs;

   @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Application> applications;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Resume> resumes;

}
