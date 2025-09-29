// Vip, Featured, Standard
package com.example.WorkWite_Repo_BE.dtos.BannerDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class BannerResponseDTO {
    private Long id;
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String bannerImage;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
class BannerResponse {
    private String company;
    private String position;
    private String startDate;
    private String endDate;
    private String status;
    private String imageUrl;
}
