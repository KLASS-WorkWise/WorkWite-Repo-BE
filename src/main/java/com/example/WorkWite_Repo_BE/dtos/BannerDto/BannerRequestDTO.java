
package com.example.WorkWite_Repo_BE.dtos.BannerDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerRequestDTO {
    // Thông tin công ty
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;

    // Thông tin banner
    private String bannerTitle;
    private String bannerImage; // URL hoặc file
    private String bannerLink;
    private String position;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private Long amount;
    private String description;

        private String bannerType; // Vip, Featured, Standard

    // Trạng thái
    private String status; // mặc định "pending"
    private Long userId;
}
