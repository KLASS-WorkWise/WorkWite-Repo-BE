package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserJpaRepository userRepository;
    private final CandidateJpaRepository candidateRepository;
    private final EmployersJpaRepository EmployersJpaRepository;

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
        return EmployersJpaRepository.findByUserUsername(username)
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
