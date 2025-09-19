package com.example.WorkWite_Repo_BE.services;



import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserJpaRepository userRepository;
    private final CandidateJpaRepository candidateJpaRepository;

    public Long getCurrentUserCandidateId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔑 Current username = " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        System.out.println("👤 Found User ID = " + user.getId());

        Candidate candidate = candidateJpaRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));

        System.out.println("📌 Candidate ID = " + candidate.getId());

        return candidate.getId();
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));
    }

    public String getCurrentUserFullName() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));
        Candidate candidate = candidateJpaRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));
        return candidate.getUser().getFullName();
    }




//    public String getChangedBy() {
//        User user = getCurrentUser();
//        String role = user.getRoles().name(); // giả sử User có field role (enum hoặc string)
//
//        if (role.equalsIgnoreCase("CANDIDATE")) {
//            return "Candidate - " + user.getUsername();
//        } else if (role.equalsIgnoreCase("HR") || role.equalsIgnoreCase("ADMIN")) {
//            return "HR - " + user.getUsername();
//        } else {
//            return "System";
//        }
//    }

}

//
//public Long getCurrentUserCandidateId() {
//    String username = SecurityContextHolder.getContext().getAuthentication().getName();
//    return candidateJpaRepository.findByUserUsername(username) // custom query
//            .map(Candidate::getId)
//            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));
//}}
//Ưu điểm: chỉ query DB 1 lần thay vì 2 lần (User + Candidate).
//Cần thêm query trong CandidateJpaRepository:
//Optional<Candidate> findByUserUsername(String username);

