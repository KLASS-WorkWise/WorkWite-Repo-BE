package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String username;
    private String email;
    private String password;
}
