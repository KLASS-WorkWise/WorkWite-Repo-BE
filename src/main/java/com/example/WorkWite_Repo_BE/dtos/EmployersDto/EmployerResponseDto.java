package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerResponseDto {
    private String status;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String username;
    private String password;
    private String fullName;

}

