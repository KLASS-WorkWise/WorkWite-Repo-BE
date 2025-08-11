package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private LoggedInUserDto loggedInUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoggedInUserDto {
        private Long id;
        private String fullName;
        private String username;
        private String status;
        private List<String> roles;
    }

    @Data
    public static class RoleDto {
        // Đã bỏ, không dùng nữa
    }

    private String access_token;
    private String refresh_token;
}
