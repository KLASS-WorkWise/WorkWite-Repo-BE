package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.LoginResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RegisterResponseDto;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    // login 
    public LoginResponseDto login(LoginRequestDto request) {
    User user = userJpaRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED));

    if (!request.getPassword().equals(user.getPassword())) {
        throw new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    // Tạo JWT tokens
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // Lấy roles của user
    List<String> roles = user.getRoles() != null ? 
        user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()) : 
        List.of("Users");

    LoginResponseDto.LoggedInUserDto loggedInUser = LoginResponseDto.LoggedInUserDto.builder()
        .id(user.getId())
        .fullname(user.getFullName())
        .username(user.getUsername())
        .status("active")
        .email(user.getEmail())
        .roles(roles)
        .build();

    return LoginResponseDto.builder()
        .access_token(accessToken)
        .refresh_token(refreshToken)
        .loggedInUser(loggedInUser)
        .build();
    }

    private final UserJpaRepository userRepository;
    private final CandidateJpaRepository candidateRepository;
    private final EmployersJpaRepository employersJpaRepository;
    private final JwtService jwtService;
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final CandidatesServices candidatesServices;
    private final MailService mailService;

    // register 
    public Map<String, Object> register(RegisterRequestDto request) {
        // email đã tồn tại
        if (userJpaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // Gán role mặc định nếu có
        Role userRole = roleJpaRepository.findByName("Users").orElseGet(() -> {
            Role role = new Role();
            role.setName("Users");
            return roleJpaRepository.save(role);
        });
        user.setRoles(List.of(userRole));

        userJpaRepository.save(user);
        candidatesServices.createCandidateForUser(user);

        Map<String, Object> responseRegister = Map.of(
            "message", "Đăng ký thành công!",
            "data", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullname", user.getFullName(),
                "username", user.getUsername()
                )
                );
        return responseRegister;
    }

    // Gửi mã 6 số về email để reset password
    public Map<String, Object> forgotPassword(String email) {
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

        return Map.of("message", "Reset code sent to email");
    }

    // Xác thực mã 6 số và đổi mật khẩu
    public Map<String, Object> verifyResetCode(String email, String code, String newPassword) {
        User user = userJpaRepository.findByUserEmail(email)
                .orElseThrow(() -> new HttpException("Email not found", HttpStatus.NOT_FOUND));

        // Kiểm tra mã và hạn sử dụng
        if (user.getResetCode() == null || user.getResetCodeExpiry() == null ||
                !user.getResetCode().equals(code) ||
                user.getResetCodeExpiry() < System.currentTimeMillis()) {
            return Map.of("status", "error", "message", "Invalid or expired reset code");
        }

        // Đổi mật khẩu
        user.setPassword(newPassword);
        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userJpaRepository.save(user);

        return Map.of("message", "Password reset successful");
    }

    // ✅ Lấy User hiện tại
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));
    }

    // ✅ Lấy CandidateId hiện tại (dành cho ứng viên apply job)
    public Long getCurrentUserCandidateId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return candidateRepository.findByUserUsername(username)
                .map(Candidate::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));
    }

    // ✅ Lấy EmployerId hiện tại (dành cho HR/Employer quản lý job)
    public Long getCurrentUserEmployerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return employersJpaRepository.findByUserUsername(username)
        .map(Employers::getId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer không tồn tại"));
    }

    // ✅ Lấy Full Name user hiện tại
    public String getCurrentUserFullName() {
        return getCurrentUser().getFullName();
    }

    // ✅ Helper: phân biệt role (CANDIDATE, EMPLOYER, ADMIN)
    public String getCurrentUserRole() {
        User user = getCurrentUser();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User chưa có role");
        }
        return user.getRoles().get(0).getName(); // ví dụ: "CANDIDATE"
    }




}
