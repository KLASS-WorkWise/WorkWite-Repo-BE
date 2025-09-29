package com.example.WorkWite_Repo_BE.dtos.SystemLogDto;

import lombok.Data;

@Data
public class SystemLogResponseDTO {
    private Long id;
    private String actor;
    private String action;
    private String description;
    private String status;
    private String timestamp; // Đổi sang String để trả về định dạng đẹp
}
