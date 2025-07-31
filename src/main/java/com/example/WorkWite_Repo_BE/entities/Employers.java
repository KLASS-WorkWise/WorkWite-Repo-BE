package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employers")
@Data
public class Employers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "culture", columnDefinition = "TEXT")
    private String culture;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "media_urls", columnDefinition = "TEXT")
    private String mediaUrls;

    @Column(name = "email", columnDefinition = "TEXT")
    private String email;
}