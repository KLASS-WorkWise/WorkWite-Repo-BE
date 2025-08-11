package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String username;
    private String password;
    private String email;
    private String fullName;
}
