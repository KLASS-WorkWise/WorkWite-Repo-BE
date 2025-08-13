package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInformationJpaRepository extends JpaRepository<CompanyInformation, Long> {
    Page<CompanyInformation> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
}
