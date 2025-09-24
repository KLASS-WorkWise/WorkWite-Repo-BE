
package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Applicant;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.WorkWite_Repo_BE.entities.JobPosting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
    Optional<JobPosting> findByIdAndEmployer_Id(Long jobId, Long employerId);
	// đếm số lượng job posting đã đdăngddee stats
	@Query("SELECT MONTH(j.createdAt) as month, COUNT(j) as value FROM JobPosting j WHERE YEAR(j.createdAt) = :year GROUP BY MONTH(j.createdAt)")
	List<Object[]> countJobPostingByMonth(@Param("year") int year);

    Page<JobPosting> findByEmployer_Id(Long employerId, Pageable pageable);
//    List<JobPosting> findByApplicantId(Long ApplicantId);


    // ✅ sort theo đơn ứng tuyển gần nhất
    @Query("""
        SELECT jp
        FROM JobPosting jp
        LEFT JOIN jp.applicants a
        WHERE jp.employer.id = :employerId
        GROUP BY jp
        ORDER BY MAX(a.appliedAt) DESC
    """)
    Page<JobPosting> findAllOrderByLastAppliedAtDesc(
            @Param("employerId") Long employerId,
            Pageable pageable
    );

    @Query("""
        SELECT jp
        FROM JobPosting jp
        LEFT JOIN jp.applicants a
        WHERE jp.employer.id = :employerId
        GROUP BY jp
        ORDER BY MAX(a.appliedAt) ASC
    """)
    Page<JobPosting> findAllOrderByLastAppliedAtAsc(
            @Param("employerId") Long employerId,
            Pageable pageable
    );

    // ✅ sort theo trạng thái đơn (ví dụ: ưu tiên job có nhiều đơn PENDING trước)
    @Query("""
        SELECT jp
        FROM JobPosting jp
        LEFT JOIN jp.applicants a
        WHERE jp.employer.id = :employerId
        GROUP BY jp
        ORDER BY SUM(CASE WHEN a.applicationStatus = 'PENDING' THEN 1 ELSE 0 END) DESC
    """)
    Page<JobPosting> findAllOrderByPendingApplicantsDesc(
            @Param("employerId") Long employerId,
            Pageable pageable
    );

    //loc theo ngay
    @Query("""
    SELECT jp
    FROM JobPosting jp
    WHERE jp.employer.id = :employerId
    AND (:status IS NULL OR jp.status = :status)
    AND (
        :isExpired IS NULL
        OR (:isExpired = true AND jp.endAt < CURRENT_TIMESTAMP)
        OR (:isExpired = false AND jp.endAt >= CURRENT_TIMESTAMP)
    )
    AND (:startDate IS NULL OR jp.endAt >= :startDate)
    AND (:endDate IS NULL OR jp.endAt <= :endDate)
""")
    Page<JobPosting> findByEmployerAndFilters(
            @Param("employerId") Long employerId,
            @Param("status") String status,
            @Param("isExpired") Boolean isExpired,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    // ✅ sort theo tổng số đơn apply (nhiều nhất → ít nhất)
    @Query("""
    SELECT jp
    FROM JobPosting jp
    LEFT JOIN jp.applicants a
    WHERE jp.employer.id = :employerId
    GROUP BY jp
    ORDER BY COUNT(a) DESC
""")
    Page<JobPosting> findAllOrderByApplicantsCountDesc(
            @Param("employerId") Long employerId,
            Pageable pageable
    );

    // ✅ sort theo tổng số đơn apply (ít nhất → nhiều nhất)
    @Query("""
    SELECT jp
    FROM JobPosting jp
    LEFT JOIN jp.applicants a
    WHERE jp.employer.id = :employerId
    GROUP BY jp
    ORDER BY COUNT(a) ASC
""")
    Page<JobPosting> findAllOrderByApplicantsCountAsc(
            @Param("employerId") Long employerId,
            Pageable pageable
    );



}