package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.ApplicantHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicantHistoryRepository extends JpaRepository<ApplicantHistory, Long> {
    List<ApplicantHistory> findByApplicantIdOrderByUpdatedAt(Long applicantId);
    Optional<ApplicantHistory> findByApplicantIdAndStep(Long applicantId, String step);
}