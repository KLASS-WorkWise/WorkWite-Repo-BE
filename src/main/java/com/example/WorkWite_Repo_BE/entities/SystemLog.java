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

    private Long userId; // Id người thực hiện
    private String username; // Username/email người thực hiện
    private String action; // Hành động (LOGIN_SUCCESS, DELETE_JOB_POST...)
    private String description; // Mô tả chi tiết
    private String status; // SUCCESS, FAIL
    private LocalDateTime timestamp; // Thời gian thực hiện
}
