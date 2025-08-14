package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillJpaRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByResumeId(Long resumeId);

    @Modifying
    @Query("DELETE FROM Skill s WHERE s.resume.id = :resumeId")
    void deleteByResumeId(@Param("resumeId") Long resumeId);
}
