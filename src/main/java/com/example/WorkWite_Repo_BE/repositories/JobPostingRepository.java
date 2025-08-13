package com.example.WorkWite_Repo_BE.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.WorkWite_Repo_BE.entities.JobPosting;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
	Page<JobPosting> findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredDegreeContaining(
		String category,
		String location,
		String salaryRange,
		String jobType,
		String requiredDegree,
		Pageable pageable
	);

	Page<JobPosting> findByMinExperienceGreaterThanEqual(Integer minExperience, Pageable pageable);

	// Có thể bổ sung thêm các phương thức filter khác nếu cần
}