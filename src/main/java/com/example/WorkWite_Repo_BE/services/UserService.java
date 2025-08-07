package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
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

    private UserResponseDto convertUserDto(User user) {
        // Map User.username -> UserResponseDto.name, roles để empty list
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null
                
                // java.util.Collections.emptyList());
        );
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

        return LoginResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .build();
    }

    public void register(RegisterRequestDto request) {
        if (userJpaRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        Role userRole = roleJpaRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleJpaRepository.save(r);
        });
        user.setRoles(List.of(userRole));
        userJpaRepository.save(user);
    }

    public List<UserResponseDto> getAllUser() {
        List<User> user = this.userJpaRepository.findAll();
        return user.stream()
                .map(this::convertUserDto)
                .collect(Collectors.toList());
    }
}