package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidate")
public class Candidates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private int userId;
    private String phone;
    private String address;
    private String bio;
    private String experience;
    private String education;
    private String skills;

    public Candidates(String phone, String address, String bio, String experience, String education, String skills) {
        this.phone = phone;
        this.address = address;
        this.bio = bio;
        this.experience = experience;
        this.education = education;
        this.skills = skills;
    }
}
