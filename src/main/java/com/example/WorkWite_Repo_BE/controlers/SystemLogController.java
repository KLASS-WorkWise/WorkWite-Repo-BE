package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogResponseDTO;
import com.example.WorkWite_Repo_BE.services.SystemLogServiceCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-logs")
@RequiredArgsConstructor
public class SystemLogController {
    private final SystemLogServiceCustom systemLogService;

    @GetMapping
    public ResponseEntity<List<SystemLogResponseDTO>> getAllLogs(
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        java.time.LocalDateTime startDate = start != null ? java.time.LocalDateTime.parse(start) : null;
        java.time.LocalDateTime endDate = end != null ? java.time.LocalDateTime.parse(end) : null;
        if (actor != null || status != null || start != null || end != null) {
            return ResponseEntity.ok(systemLogService.searchLogs(actor, status, startDate, endDate));
        }
        return ResponseEntity.ok(systemLogService.getAllLogs());
    }
}
