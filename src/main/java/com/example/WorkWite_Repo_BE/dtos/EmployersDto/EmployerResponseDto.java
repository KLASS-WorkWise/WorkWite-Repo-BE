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
    private Boolean status;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String username;
    private String password;
    private String fullName;

    public EmployerResponseDto(String username, String password, String fullName,
                               String email, String phoneNumber, String avatar, boolean status) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.status = status;
    }
}

