package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.SystemLogResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.SystemLogDto.PaginatedSystemLogResponseDto;
import com.example.WorkWite_Repo_BE.services.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/system-logs")
@RequiredArgsConstructor
public class SystemLogController {
    private final SystemLogService systemLogService;

    @GetMapping()
    public ResponseEntity<PaginatedSystemLogResponseDto> getAllLogsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(systemLogService.getAllLogsPaginated(page, size));
    }
}
