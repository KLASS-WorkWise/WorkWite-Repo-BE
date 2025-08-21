package com.example.WorkWite_Repo_BE.dtos.CompanyInformation;

import com.example.WorkWite_Repo_BE.entities.Employers;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
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
    private Long id;
    private String companyName;
    private String logoUrl;
    private String bannerUrl;
    private String email;
    private String phone;
    private String description;
    private Integer minEmployees;
    private Integer maxEmployees;
    private String address;
    private String location;
    private String website;
    private String industry;
    private String status;
    private Employers employer;
}
