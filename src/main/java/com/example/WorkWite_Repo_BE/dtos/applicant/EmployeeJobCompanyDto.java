package com.example.WorkWite_Repo_BE.dtos.applicant;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmployeeJobCompanyDto {
    private Long id;
    private Long employerId;
    private String employerName;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private String jobType;
    private String category;
    private List<String> requiredSkills;
    private Integer minExperience;
    private String requiredDegree;
    private LocalDateTime endAt;
    private String status;
    private LocalDateTime createdAt;
    // Thêm employer + company info
    private String employerStatus;

    private String companyName;
    private String companyLogo;
    private String companyDescription;
    private String companyLocation;

}

