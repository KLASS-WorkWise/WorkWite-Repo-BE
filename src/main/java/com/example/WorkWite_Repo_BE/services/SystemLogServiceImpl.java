package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogResponseDTO;
import com.example.WorkWite_Repo_BE.entities.SystemLog;
import com.example.WorkWite_Repo_BE.repositories.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogServiceCustom {
    private final SystemLogRepository systemLogRepository;

    // ...existing code...

    @Override
    public List<SystemLogResponseDTO> getAllLogs() {
        return systemLogRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogResponseDTO> searchLogs(String actor, String status, LocalDateTime start, LocalDateTime end) {
        return systemLogRepository.searchLogs(actor, status, start, end)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ...existing code...

    private SystemLogResponseDTO toDTO(SystemLog log) {
    SystemLogResponseDTO dto = new SystemLogResponseDTO();
    dto.setId(log.getId());
    dto.setActor(log.getUsername());
    dto.setAction(log.getAction());
    dto.setDescription(log.getDescription());
    dto.setStatus(log.getStatus());
    dto.setTimestamp(log.getTimestamp() != null ? log.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
    return dto;
    }
}
