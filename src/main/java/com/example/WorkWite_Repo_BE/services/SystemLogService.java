package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.SystemLog;
import com.example.WorkWite_Repo_BE.repositories.SystemLogRepository;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.PaginatedSystemLogResponseDto;
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

    public PaginatedSystemLogResponseDto getAllLogsPaginated(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, size);
        org.springframework.data.domain.Page<SystemLog> logPage = systemLogRepository.findAll(pageable);
        List<SystemLogResponseDTO> logDtos = logPage.getContent().stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
        PaginatedSystemLogResponseDto dto = new PaginatedSystemLogResponseDto();
        dto.setData(logDtos);
        dto.setPageNumber(logPage.getNumber() + 1);
        dto.setPageSize(logPage.getSize());
        dto.setTotalRecords(logPage.getTotalElements());
        dto.setTotalPages(logPage.getTotalPages());
        dto.setHasNext(logPage.hasNext());
        dto.setHasPrevious(logPage.hasPrevious());
        return dto;
    }

    public List<SystemLogResponseDTO> searchLogs(String actor, String status, LocalDateTime start, LocalDateTime end) {
        return systemLogRepository.searchLogs(actor, status, start, end)
                .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

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
