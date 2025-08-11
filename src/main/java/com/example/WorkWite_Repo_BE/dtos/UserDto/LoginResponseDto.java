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
    private String access_token;
    private String refresh_token;
    private LoggedInUserDto loggedInUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoggedInUserDto {
        private Long id;
        private String username;
        private Boolean isActive;
        private List<RoleDto> roles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoleDto {
        private Long id;
        private String name;
    }
}
