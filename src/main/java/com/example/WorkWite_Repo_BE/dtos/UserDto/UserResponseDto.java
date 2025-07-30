package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Data;
import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private List<String> roles;


    public UserResponseDto(Long id, String username, String email, String phone, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
    }
}
