package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationJpaRepository extends JpaRepository<Education, Integer> {
    List<Education> findByResumeId(Long resumeId);
}
