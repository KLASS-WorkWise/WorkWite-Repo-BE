package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "role",  fetch = FetchType.LAZY)  // nó chỉ lấy dữ liệu và đổ ra dlieu có trong bảng role ko kéo theo các bảng kháccó liên kết vs role
    private List<UserRole> userRoles;
}