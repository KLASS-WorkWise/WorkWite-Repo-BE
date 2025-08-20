package com.example.movie.repositories;

import com.example.movie.entities.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByCandidateId(Long candidateId);

    Optional<SavedJob> findByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);
}
