package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.UserRole;
import com.example.WorkWite_Repo_BE.entities.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleJpaResponsitory extends JpaRepository<UserRole, UserRoleId> {
    boolean existsById(UserRoleId id);

    void deleteById(UserRoleId id);
}
