package com.example.WorkWite_Repo_BE.repositories;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    boolean existsByJobPostingIdAndCandidateId(Long jobPostingId, Long candidateId);

    Page<Applicant> findByCandidateId(Long candidateId , Pageable pageable);

//    List<Applicant> findByCandidateId(Long candidateId);
}