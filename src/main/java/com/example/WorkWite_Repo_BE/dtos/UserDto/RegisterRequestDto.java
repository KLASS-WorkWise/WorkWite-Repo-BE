package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String repassword;
    // private String role; // hoặc List<String> roles nếu muốn chọn nhiều role

}
