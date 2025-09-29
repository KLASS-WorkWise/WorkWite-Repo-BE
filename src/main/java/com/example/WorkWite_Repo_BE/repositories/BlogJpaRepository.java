package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.BLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogJpaRepository extends JpaRepository<BLog, Long> {
    Optional<BLog> findBySlug(String slug);
}
