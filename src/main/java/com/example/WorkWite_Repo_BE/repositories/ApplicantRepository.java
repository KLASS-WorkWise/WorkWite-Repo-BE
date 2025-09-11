package com.example.WorkWite_Repo_BE.repositories;
import com.example.WorkWite_Repo_BE.dtos.applicant.ListApplicantResponseDTO;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    boolean existsByJobPostingIdAndCandidateId(Long jobPostingId, Long candidateId);

    Page<Applicant> findByCandidateId(Long candidateId , Pageable pageable);

//    List<Applicant> findByCandidateId(Long candidateId);

    @Query("SELECT new com.example.WorkWite_Repo_BE.dtos.applicant.ListApplicantResponseDTO( " +
            "jp.id, jp.title, c.id, u.fullName, u.email, c.phoneNumber, c.avatar, " +
            "a.coverLetter, a.resumeLink, a.applicationStatus, a.appliedAt) " +
            "FROM Applicant a " +
            "JOIN a.jobPosting jp " +
            "JOIN jp.employer e " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "WHERE e.id = :employerId " +
            "AND (:jobPostingId IS NULL OR jp.id = :jobPostingId) " +
            "AND (:status IS NULL OR a.applicationStatus = :status) " +
            "AND a.appliedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY a.appliedAt DESC")
    Page<ListApplicantResponseDTO> findApplicantsByEmployerAndDateRange(
            @Param("employerId") Long employerId,
            @Param("jobPostingId") Long jobPostingId,
            @Param("status") com.example.WorkWite_Repo_BE.enums.ApplicationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}