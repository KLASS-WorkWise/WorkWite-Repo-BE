package com.example.WorkWite_Repo_BE.repositories;
import com.example.WorkWite_Repo_BE.dtos.applicant.ListApplicantResponseDTO;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    /**
     * Lấy danh sách các công ty (employer) được ứng viên apply nhiều nhất
     */
    @Query("SELECT e.id, e.companyInformation.companyName, COUNT(a) as totalApply " +
            "FROM Applicant a " +
            "JOIN a.jobPosting jp " +
            "JOIN jp.employer e " +
            "GROUP BY e.id, e.companyInformation.companyName " +
            "ORDER BY totalApply DESC")
    List<Object[]> findTopCompaniesByApplications();
    boolean existsByJobPostingIdAndCandidateId(Long jobPostingId, Long candidateId);

    Page<Applicant> findByCandidateId(Long candidateId , Pageable pageable);

    Optional<Applicant> findByIdAndCandidateId(Long id, Long candidateId);;
    List<Applicant> findByJobPostingId(Long jobPostingId);
    // ✅ đếm đơn chưa đọc
    Long countByJobPostingIdAndIsReadFalse(Long jobId);
    @Modifying
    @Query("UPDATE Applicant a SET a.isRead = true WHERE a.jobPosting.id = :jobId")
    void markAllAsReadByJob(@Param("jobId") Long jobId);

    @Query("SELECT MAX(a.appliedAt) FROM Applicant a WHERE a.jobPosting.id = :jobId")
    LocalDateTime findLastAppliedAtByJobId(@Param("jobId") Long jobId);

    // đếm số lượng appli HIRED
    @Query("SELECT MONTH(a.appliedAt) as month, COUNT(a) as value FROM Applicant a WHERE YEAR(a.appliedAt) = :year GROUP BY MONTH(a.appliedAt)")
    List<Object[]> countApplicantByMonth(@Param("year") int year);
    Long countByJobPostingId(Long jobId);
    Long countByJobPostingIdAndApplicationStatus(Long jobPostingId, ApplicationStatus status);





    long countByApplicationStatus(ApplicationStatus status);

    List<Applicant> findByExperienceYearsGreaterThanEqual(int years);

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

    List<Applicant> findByJobPostingIdAndApplicationStatus(Long jobId, ApplicationStatus status);

    @Query("SELECT a.applicationStatus, COUNT(a) " +
            "FROM Applicant a " +
            "WHERE a.jobPosting.id = :jobId " +
            "GROUP BY a.applicationStatus")
    List<Object[]> countApplicantsByStatus(@Param("jobId") Long jobId);

}