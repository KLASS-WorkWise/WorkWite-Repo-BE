package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ActivityJpaRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByResumeId(Long resumeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Activity a WHERE a.resume.id = :resumeId")
    void deleteByResumeId(@Param("resumeId") Long resumeId);
}
