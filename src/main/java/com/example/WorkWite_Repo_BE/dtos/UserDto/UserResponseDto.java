package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private java.util.List<String> roles;
}