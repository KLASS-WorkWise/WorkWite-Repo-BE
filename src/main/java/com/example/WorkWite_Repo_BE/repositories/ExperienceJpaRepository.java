package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Activity;
import com.example.WorkWite_Repo_BE.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceJpaRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByResumeId(Long resumeId);
}
