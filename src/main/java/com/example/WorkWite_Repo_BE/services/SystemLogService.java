package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.SystemLog;
import com.example.WorkWite_Repo_BE.repositories.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemLogService {
    private final SystemLogRepository systemLogRepository;

    public SystemLog saveLog(Long userId, String username, String action, String description, String status) {
        SystemLog log = SystemLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .description(description)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
        return systemLogRepository.save(log);
    }

    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAll();
    }
}
