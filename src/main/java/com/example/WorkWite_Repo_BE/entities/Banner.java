package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.example.WorkWite_Repo_BE.enums.BannerStatus;

@Entity
@Table(name = "banners")
@Getter
@Setter
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông tin công ty
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;

    // Thông tin banner
    private String bannerTitle;
    private String bannerImage;
    private String bannerLink;
    private String position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long amount;
    private String description;
    private String bannerType; // Vip, Featured, Standard

    @Enumerated(EnumType.STRING)
    private BannerStatus status; // PENDING, ACTIVE, REJECTED, EXPIRED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
