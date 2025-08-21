package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Employers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployersJpaRepository extends JpaRepository<Employers, Long> {
    boolean existsByUserId(Long userId);
    Optional<Employers> findByUserId(Long userId);
    Page<Employers> findByStatus(String status, Pageable pageable);
}