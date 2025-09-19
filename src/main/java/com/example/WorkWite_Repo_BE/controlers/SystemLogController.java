package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.entities.SystemLog;
import com.example.WorkWite_Repo_BE.services.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class SystemLogController {
    private final SystemLogService systemLogService;

    // Lấy tất cả log
    @GetMapping
    public ResponseEntity<List<SystemLog>> getAllLogs() {
        return ResponseEntity.ok(systemLogService.getAllLogs());
    }

    // Tìm kiếm log theo tiêu chí
    @GetMapping("/search")
    public ResponseEntity<List<SystemLog>> searchLogs(
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(systemLogService.searchLogs(actor, level, action, start, end));
    }
}
