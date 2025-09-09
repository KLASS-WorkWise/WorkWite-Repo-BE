package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterResponseDto;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import com.example.WorkWite_Repo_BE.services.JwtService;
import com.example.WorkWite_Repo_BE.services.MailService;
import com.example.WorkWite_Repo_BE.services.UserService;
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

    private final UserService userService;
    private final JwtService jwtService;
    private final UserJpaRepository userJpaRepository;
    private final MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) throws Exception {
        LoginResponseDto result = this.userService.login(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        if (userJpaRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email đã tồn tại!"));
        }
        RegisterResponseDto response = userService.register(request); // gọi hàm bên service để lưu vào database
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
    }

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

    // Gửi mã 6 số về email để reset password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userJpaRepository.findByUserEmail(email)
                .orElseThrow(() -> new HttpException("Email not found", HttpStatus.NOT_FOUND));

        // Sinh mã 6 số ngẫu nhiên
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // Lưu mã này vào DB
        user.setResetCode(code);
        user.setResetCodeExpiry(System.currentTimeMillis() + 15 * 60 * 1000); // Hết hạn sau 15 phút
        userJpaRepository.save(user);

        // Gửi email chứa mã 6 số
        mailService.sendMail(email, "Reset Password - JobBox",
                "<div style='font-family:sans-serif;padding:16px;border-radius:8px;border:1px solid #eee;max-width:900px;'>"
                        + "<h2 style='color:#2b6cb0;'>JobBox - Password Reset</h2>"
                        + "<p>Xin chào,</p>"
                        + "<p>Bạn vừa yêu cầu đặt lại mật khẩu. Mã xác thực của bạn là:</p>"
                        + "<div style='font-size:24px;font-weight:bold;color:#e53e3e;margin:16px 0;'>" + code + "</div>"
                        + "<p>Mã này có hiệu lực trong 15 phút.</p>"
                        + "<p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>"
                        + "<hr style='margin:16px 0;'>"
                        + "<small>JobBox Team</small>"
                        + "</div>");

        return ResponseEntity.ok(Map.of("message", "Reset code sent to email"));
    }

    // Xác thực mã 6 số và đổi mật khẩu
    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");

        User user = userJpaRepository.findByUserEmail(email)
                .orElseThrow(() -> new HttpException("Email not found", HttpStatus.NOT_FOUND));

        // Kiểm tra mã và hạn sử dụng
        if (user.getResetCode() == null || user.getResetCodeExpiry() == null ||
                !user.getResetCode().equals(code) ||
                user.getResetCodeExpiry() < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired reset code");
        }

        // Đổi mật khẩu
        user.setPassword(newPassword);
        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userJpaRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}