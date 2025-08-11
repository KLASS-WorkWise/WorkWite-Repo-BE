package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String username;
    private String password;
    // private String role; // hoặc List<String> roles nếu muốn chọn nhiều role

}

