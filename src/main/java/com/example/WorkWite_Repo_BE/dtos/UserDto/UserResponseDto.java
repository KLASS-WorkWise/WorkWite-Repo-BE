package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String address;
}