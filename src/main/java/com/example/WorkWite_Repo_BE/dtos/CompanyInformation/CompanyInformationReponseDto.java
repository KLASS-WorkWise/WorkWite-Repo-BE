package com.example.WorkWite_Repo_BE.dtos.CompanyInformation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInformationReponseDto {
    private Integer employee;
    private String companyName;
    private String logoUrl;
    private String bannerUrl;
    private String email;
    private Integer phone;
    private String description;
    private LocalDateTime lastPosted;
    private String address;
    private String location;
    private String website;
    private String industry;
}
