package com.example.WorkWite_Repo_BE.repositories;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppJpaRepository extends JpaRepository<Applicant, Integer> {

}
