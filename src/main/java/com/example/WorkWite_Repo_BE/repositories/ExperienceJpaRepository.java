package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Activity;
import com.example.WorkWite_Repo_BE.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExperienceJpaRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByResumeId(Long resumeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Experience e WHERE e.resume.id = :resumeId")
    void deleteByResumeId(@Param("resumeId") Long resumeId);
}
