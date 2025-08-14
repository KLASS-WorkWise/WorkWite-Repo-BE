package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardJpaRepository extends JpaRepository<Award, Long> {
    List<Award> findByResumeId(Long resumeId);

    @Modifying
    @Query("DELETE FROM Award a WHERE a.resume.id = :resumeId")
    void deleteByResumeId(@Param("resumeId") Long resumeId);
}
