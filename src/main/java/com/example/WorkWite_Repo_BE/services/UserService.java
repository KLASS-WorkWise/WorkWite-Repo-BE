
package com.example.WorkWite_Repo_BE.services;


import com.example.WorkWite_Repo_BE.dtos.UserDto.*;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final CandidatesServices candidatesServices;


    // Chuẩn hóa hàm convertToDto cho User entity
    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setStatus(user.getStatus());
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = this.userJpaRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long id, UserUpdateRequestDto request) {
        User user = userJpaRepository.findById(id)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getPassword() != null)
            user.setPassword(request.getPassword());
        userJpaRepository.save(user);
        return convertToDto(user);
    }

    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new HttpException("User not found", HttpStatus.NOT_FOUND);
        }
        userJpaRepository.deleteById(id);
    }

    public LoginResponseDto login(LoginRequestDto request) throws Exception {
        // Find the user by email (username)
        User user = this.userJpaRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        // Verify password
        if (!request.getPassword().equals(user.getPassword())) {
            throw new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        // Generate a new access token (with full data + roles)
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = "dummy_refresh_token"; // TODO: sinh refresh token thực tế nếu có

        // Map roles: chỉ lấy tên role
        List<String> roles = user.getRoles() != null ? user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()) : null;

        LoginResponseDto.LoggedInUserDto loggedInUser = LoginResponseDto.LoggedInUserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .status("active")
                .roles(roles)
                .build();

        return LoginResponseDto.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .loggedInUser(loggedInUser)
                .build();
    }

    public RegisterResponseDto register(RegisterRequestDto request) {
        if (!request.getPassword().equals(request.getRepassword())) {
            throw new RuntimeException("Mật khẩu nhập lại không khớp!");
        }
        if (userJpaRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // Gán role mặc định nếu có
        Role userRole = roleJpaRepository.findByName("Users").orElseGet(() -> {
            Role r = new Role();
            r.setName("Users");
            return roleJpaRepository.save(r);
        });
        user.setRoles(List.of(userRole));

        userJpaRepository.save(user);
        candidatesServices.createCandidateForUser(user);

        return new RegisterResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUsername());
    }
}
