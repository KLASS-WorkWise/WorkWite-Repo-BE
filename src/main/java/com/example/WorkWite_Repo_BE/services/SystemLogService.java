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

    public SystemLog saveLog(String actor, String action, String description, String ipAddress, String level,
            Long targetUserId) {
        SystemLog log = SystemLog.builder()
                .actor(actor)
                .action(action)
                .description(description)
                .ipAddress(ipAddress)
                .timestamp(LocalDateTime.now())
                .level(level)
                .targetUserId(targetUserId)
                .build();
        return systemLogRepository.save(log);
    }

    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAll();
    }

    public List<SystemLog> searchLogs(String actor, String level, String action, LocalDateTime start,
            LocalDateTime end) {
        return systemLogRepository.searchLogs(actor, level, action, start, end);
    }
}
