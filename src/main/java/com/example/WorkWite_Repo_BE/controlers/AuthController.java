package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterResponseDto;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import com.example.WorkWite_Repo_BE.services.JwtService;
import com.example.WorkWite_Repo_BE.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // cấu hình để chấp nhận địa chỉ 5173 đi qua
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
        private final JwtService jwtService;
    private final UserJpaRepository userJpaRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) throws Exception {
        LoginResponseDto result = this.userService.login(request);
        return ResponseEntity.ok(result);
    }

    // ...existing code...

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto request) {
        RegisterResponseDto response = this.userService.register(request);
        return ResponseEntity.ok(response);
    }

    //  refreshtoken
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        String username = jwtService.extractUsername(refreshToken);
        User user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        if (!jwtService.isRefreshTokenValid(refreshToken, user.getUsername())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
        String newAccessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    }
    
}
