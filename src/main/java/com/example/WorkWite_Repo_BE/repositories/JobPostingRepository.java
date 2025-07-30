package com.example.WorkWite_Repo_BE.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.WorkWite_Repo_BE.entities.JobPosting;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
}