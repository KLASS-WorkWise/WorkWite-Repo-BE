
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserUpdateRequestDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterResponseDto;

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

    public List<UserResponseDto> getAllUsers() {
        List<User> users = this.userJpaRepository.findAll();
        return users.stream()
                .map(this::convertUserDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto createUser(CreateUserRequestDto request) {
        if (userJpaRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // Gán role mặc định nếu có
        Role userRole = roleJpaRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleJpaRepository.save(r);
        });
        user.setRoles(List.of(userRole));

        userJpaRepository.save(user);

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null // hoặc truyền thêm trường nếu UserResponseDto có
        );
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
        return convertUserDto(user);
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
        if (userJpaRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // Gán role mặc định nếu có
        Role userRole = roleJpaRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleJpaRepository.save(r);
        });
        user.setRoles(List.of(userRole));

        userJpaRepository.save(user);

        return new RegisterResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUsername());
    }
}
