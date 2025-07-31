package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;

    @Column(nullable = false, unique = true)  // ko dc null | phải là giá trị duy nhất ko dc trùng
    private String email;

    @Column(nullable = false)
    private String password;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER )  // một user có nhiều Role,
    // mappedBy = "user" có nghĩa là ta éo giữ khóa phụ : ta ko tạo ra khóa phụ , nó nằm ở bên bảng entity userRole
    // giúp ko cần tạo bảng trung gian dư thừa , ko có mappedBy là nó tạo thêm bảng trung gian để  tránh tạo cột thừa trong DB và ràng buộc ai là chủ của mối quan hệ.

    private List<UserRole> userRoles;


}
