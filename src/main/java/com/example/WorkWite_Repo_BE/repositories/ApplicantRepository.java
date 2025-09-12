package com.example.WorkWite_Repo_BE.repositories;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    boolean existsByJobPostingIdAndCandidateId(Long jobPostingId, Long candidateId);

    Page<Applicant> findByCandidateId(Long candidateId , Pageable pageable);

    // List<Applicant> findByCandidateId(Long candidateId);

    // đếm số lượng appli HIRED
    @Query("SELECT MONTH(a.appliedAt) as month, COUNT(a) as value FROM Applicant a WHERE YEAR(a.appliedAt) = :year GROUP BY MONTH(a.appliedAt)")
    List<Object[]> countApplicantByMonth(@Param("year") int year);

    long countByApplicationStatus(ApplicationStatus status);

//    List<Applicant> findByCandidateId(Long candidateId);
List<Applicant> findByExperienceYearsGreaterThanEqual(int years);

}