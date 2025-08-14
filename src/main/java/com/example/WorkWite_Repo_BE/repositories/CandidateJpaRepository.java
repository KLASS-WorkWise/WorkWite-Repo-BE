package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CandidateJpaRepository extends JpaRepository<Candidate, Long> {
    @Query("SELECT c FROM Candidate c JOIN c.user u JOIN u.roles r WHERE r.name = 'Users'")
    List<Candidate> findAllCandidatesWithUserRole();

    @Query("SELECT c FROM Candidate c JOIN c.user u JOIN u.roles r WHERE r.name = 'Users'")
    Page<Candidate> findAllCandidatesWithUserRole(Pageable pageable);
}
