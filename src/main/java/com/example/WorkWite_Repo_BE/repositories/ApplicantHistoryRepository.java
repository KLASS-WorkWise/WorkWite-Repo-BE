package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.ApplicantHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantHistoryRepository extends JpaRepository<ApplicantHistory, Long> {
    List<ApplicantHistory> findByApplicantIdOrderByChangedAtAsc(Long applicantId);
    List<ApplicantHistory> findByApplicantId(Long applicantId);
}
