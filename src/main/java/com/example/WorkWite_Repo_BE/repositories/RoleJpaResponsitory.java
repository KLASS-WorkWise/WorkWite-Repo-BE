package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleJpaResponsitory extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name); // để dùng trong assignRoleToUser
}
