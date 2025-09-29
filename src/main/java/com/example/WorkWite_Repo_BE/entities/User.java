package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName; // Thêm trường fullName
    private String status; // Thêm trường status

    // reset password, Để lưu mã 6 số và thời gian hết hạn.
    private String resetCode;
    private Long resetCodeExpiry;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String avatarUrl; // Đường dẫn hoặc URL ảnh đại diện

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Số dư tài khoản (quản lý nạp/trừ tiền)
    @Column(nullable = true)
    private Long balance = 0L;

}