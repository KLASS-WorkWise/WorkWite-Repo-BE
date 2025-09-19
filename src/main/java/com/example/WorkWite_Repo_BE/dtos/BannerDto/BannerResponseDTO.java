// Vip, Featured, Standard
package com.example.WorkWite_Repo_BE.dtos.BannerDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerResponseDTO {
    private Long id;
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;
    private String bannerTitle;
    private String bannerImage;
    private String bannerLink;
    private String position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;
        private String bannerType; 
}
