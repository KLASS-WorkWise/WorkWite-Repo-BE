package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor; // Người thực hiện (username hoặc email)
    private String action; // Hành động (LOGIN, CREATE_JOB, APPLY_JOB, DELETE_USER...)
    private String description; // Mô tả chi tiết
    private String ipAddress; // Địa chỉ IP
    private LocalDateTime timestamp; // Thời gian thực hiện
    private String level; // Mức độ (INFO, WARN, ERROR, SECURITY)
    private Long targetUserId; // Id user bị sửa/xóa
}
