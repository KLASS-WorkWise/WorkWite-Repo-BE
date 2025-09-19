package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBalanceService {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Transactional
    public void addBalance(Long userId, Long amount) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(user.getBalance() + amount);
        userJpaRepository.save(user);
    }

    @Transactional
    public void subtractBalance(Long userId, Long amount) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getBalance() < amount) {
            throw new RuntimeException("Not enough balance");
        }
        user.setBalance(user.getBalance() - amount);
        userJpaRepository.save(user);
    }

    public Long getBalance(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getBalance();
    }
}
