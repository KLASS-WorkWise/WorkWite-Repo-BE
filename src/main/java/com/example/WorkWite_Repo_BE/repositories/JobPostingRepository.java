
package com.example.WorkWite_Repo_BE.repositories;

import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.WorkWite_Repo_BE.entities.JobPosting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
	Page<JobPosting> findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredDegreeContaining(
			String category,
			String location,
			String salaryRange,
			String jobType,
			String requiredDegree,
			Pageable pageable);

	Page<JobPosting> findByMinExperienceGreaterThanEqual(@Param("minExperience") Integer minExperience,
			Pageable pageable);

	// Có thể bổ sung thêm các phương thức filter khác nếu cần
	Optional<JobPosting> findById(Long id);

	// đếm số lượng job posting đã đdăngddee stats
	@Query("SELECT MONTH(j.createdAt) as month, COUNT(j) as value FROM JobPosting j WHERE YEAR(j.createdAt) = :year GROUP BY MONTH(j.createdAt)")
	List<Object[]> countJobPostingByMonth(@Param("year") int year);


	
}