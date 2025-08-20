	package com.example.WorkWite_Repo_BE.repositories;
	import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
	import org.springframework.stereotype.Repository;
	import com.example.WorkWite_Repo_BE.entities.JobPosting;

	import java.util.Optional;

	@Repository
	public interface JobPostingRepository extends JpaRepository<JobPosting, Long>, JpaSpecificationExecutor<JobPosting> {
		// Hỗ trợ filter động bằng Specification
		org.springframework.data.domain.Page<JobPosting> findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredDegreeContaining(
			String category,
			String location,
			String salaryRange,
			String jobType,
			String requiredDegree,
			org.springframework.data.domain.Pageable pageable
		);

	}