package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerResponseDto {
    private Long id;

    // Thông tin user
    private String username;
    private String email;
    private String fullName;
    private String status;

    // Thông tin employers
    private String phoneNumber;
    private String avatar;




}

