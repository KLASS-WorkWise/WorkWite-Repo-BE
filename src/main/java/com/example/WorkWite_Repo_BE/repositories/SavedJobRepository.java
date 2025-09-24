package com.example.WorkWite_Repo_BE.repositories;


import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.entities.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

//    List<SavedJob> findByCandidateId(Long candidateId);


    Optional<SavedJob> findByCandidateIdAndId(Long candidateId, Long id);

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);
    Page<SavedJob> findByCandidateId(Long candidateId, Pageable pageable);
}
