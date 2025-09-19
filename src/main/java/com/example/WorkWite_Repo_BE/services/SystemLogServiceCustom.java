package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogResponseDTO;
import java.util.List;

public interface SystemLogServiceCustom {
    List<SystemLogResponseDTO> getAllLogs();
    List<SystemLogResponseDTO> searchLogs(String actor, String status, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
