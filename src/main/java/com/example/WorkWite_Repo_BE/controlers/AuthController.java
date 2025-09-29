package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterResponseDto;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import com.example.WorkWite_Repo_BE.services.AuthService;
import com.example.WorkWite_Repo_BE.services.JwtService;
import com.example.WorkWite_Repo_BE.services.MailService;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Removed unused fields
    private final com.example.WorkWite_Repo_BE.services.SystemLogService systemLogService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) throws Exception {
    LoginResponseDto result = authService.login(request);   // lấy theo tên LoginResponseDto bên file service 

        // Ghi log đăng nhập thành công
        Long userId = result.getLoggedInUser() != null ? result.getLoggedInUser().getId() : null;
        String username = request.getUsername();
        systemLogService.saveLog(
            userId,
            username,
            "LOGIN_SUCCESS",
            "User login",
            "SUCCESS"
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
    Map<String, Object> responseRegister = authService.register(request);
    return ResponseEntity.ok(responseRegister);
    }

    // @PostMapping("/refresh")
    // public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
    //     String refreshToken = body.get("refresh_token");
    //     String username = jwtService.extractUsername(refreshToken);
    //     User user = userJpaRepository.findByUsername(username)
    //             .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
    //     if (!jwtService.isRefreshTokenValid(refreshToken, user.getUsername())) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
    //     }
    //     String newAccessToken = jwtService.generateAccessToken(user);
    //     return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    // }


    // Gửi mã 6 số về email để reset password
    @PostMapping("/forgot-password")
        public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            Map<String, Object> response = authService.forgotPassword(email);
            return ResponseEntity.ok(response);
    }

    // Xác thực mã 6 số và đổi mật khẩu
    @PostMapping("/verify-reset-code")
        public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            String code = body.get("code");
            String newPassword = body.get("newPassword");
            Map<String, Object> response = authService.verifyResetCode(email, code, newPassword);
            return ResponseEntity.ok(response);
    }
}