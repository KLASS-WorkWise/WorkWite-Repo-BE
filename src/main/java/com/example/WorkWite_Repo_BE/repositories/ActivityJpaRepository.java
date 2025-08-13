package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityJpaRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByResumeId(Long resumeId);
}
