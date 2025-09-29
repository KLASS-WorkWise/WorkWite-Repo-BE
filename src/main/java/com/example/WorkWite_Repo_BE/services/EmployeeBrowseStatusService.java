package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.api.RestResponse;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeBrowseStatusService {
    private final AuthService authService;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicantRepository applicantRepository;

    public EmployeeBrowseStatusService(AuthService authService, JobPostingRepository jobPostingRepository, ApplicantRepository applicantRepository) {
        this.authService = authService;
        this.jobPostingRepository = jobPostingRepository;
        this.applicantRepository = applicantRepository;
    }

    private JobPostingResponseDTO convertToDto(JobPosting jobPosting) {
        Long applicantsCount = applicantRepository.countByJobPostingId(jobPosting.getId());
//        Long newApplicantsCount = applicantRepository
//                .countByJobPostingIdAndApplicationStatus(jobPosting.getId(), ApplicationStatus.PENDING);
        Long newApplicantsCount = applicantRepository.countByJobPostingIdAndIsReadFalse(jobPosting.getId());
        LocalDateTime lastAppliedAt = applicantRepository.findLastAppliedAtByJobId(jobPosting.getId());
        return JobPostingResponseDTO.builder()
                .id(jobPosting.getId())
                .employerName(jobPosting.getEmployer().getUser().getFullName())
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .location(jobPosting.getLocation())
                .salaryRange(jobPosting.getSalaryRange())
                .jobType(jobPosting.getJobType())
                .category(jobPosting.getCategory())
                .requiredSkills(jobPosting.getRequiredSkills())
                .minExperience(jobPosting.getMinExperience())
                .requiredDegree(jobPosting.getRequiredDegree())
                .endAt(jobPosting.getEndAt())
                .status(jobPosting.getStatus())
                .createdAt(jobPosting.getCreatedAt())
                .applicantsCount(applicantsCount)
                .newApplicantsCount(newApplicantsCount)
                .lastAppliedAt(lastAppliedAt)
                .build();
    }

    public RestResponse<PaginatedEmployeeListJobResponseDto<JobPostingResponseDTO>> getEmployerJobs(
            int page, int size, String sortBy, String sortDir,
            String status, Boolean isExpired , LocalDateTime startDate, LocalDateTime endDate
    ) {
        Long employerId = authService.getCurrentUserEmployerId();

        Pageable pageable;
        Page<JobPosting> jobPostingPage;

        if ("lastAppliedAt".equalsIgnoreCase(sortBy)) {
            pageable = PageRequest.of(page, size);
            jobPostingPage = "asc".equalsIgnoreCase(sortDir)
                    ? jobPostingRepository.findAllOrderByLastAppliedAtAsc(employerId, pageable)
                    : jobPostingRepository.findAllOrderByLastAppliedAtDesc(employerId, pageable);

        } else if ("pendingApplicants".equalsIgnoreCase(sortBy)) {
            pageable = PageRequest.of(page, size);
            jobPostingPage = jobPostingRepository.findAllOrderByPendingApplicantsDesc(employerId, pageable);

        } else if ("applicantsCount".equalsIgnoreCase(sortBy)) {
            pageable = PageRequest.of(page, size);
            jobPostingPage = "asc".equalsIgnoreCase(sortDir)
                    ? jobPostingRepository.findAllOrderByApplicantsCountAsc(employerId, pageable)
                    : jobPostingRepository.findAllOrderByApplicantsCountDesc(employerId, pageable);

        } else {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            pageable = PageRequest.of(page, size, sort);

            jobPostingPage = jobPostingRepository.findByEmployerAndFilters(
                    employerId, status, isExpired, startDate, endDate, pageable
            );
        }

        List<JobPostingResponseDTO> jobDtos = jobPostingPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        PaginatedEmployeeListJobResponseDto<JobPostingResponseDTO> pagedData = PaginatedEmployeeListJobResponseDto.<JobPostingResponseDTO>builder()
                .content(jobDtos)
                .pageNumber(jobPostingPage.getNumber())
                .pageSize(jobPostingPage.getSize())
                .totalRecords(jobPostingPage.getTotalElements())
                .totalPages(jobPostingPage.getTotalPages())
                .hasNext(jobPostingPage.hasNext())
                .hasPrevious(jobPostingPage.hasPrevious())
                .build();
        return RestResponse.<PaginatedEmployeeListJobResponseDto<JobPostingResponseDTO>>builder()
                .statusCode(200)
                .error(null)
                .message("Success")
                .data(pagedData)
                .build();
    }


    // ✅ 2. Employer xem danh sách applicant trong 1 job cụ thể
//    Employer xem danh sách applicant trong 1 job cụ thể (có lọc status)
    public RestResponse<ApplicantsWithStatsDto> getApplicantsByJob(Long jobId, ApplicationStatus status) {
        Long employerId = authService.getCurrentUserEmployerId();
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        if (!jobPosting.getEmployer().getId().equals(employerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xem job này");
        }

        // Nếu có status thì lọc theo status
        List<Applicant> applicantList = (status != null)
                ? applicantRepository.findByJobPostingIdAndApplicationStatus(jobId, status)
                : applicantRepository.findByJobPostingId(jobId);

        List<ApplicantResponseDto> applicants = applicantList.stream()
                .map(app -> ApplicantResponseDto.builder()
                        .id(app.getId())
                        .jobId(app.getJobPosting().getId())
                        .candidateId(app.getCandidate().getId())
                        .jobTitle(app.getJobPosting().getTitle())
                        .description_company(app.getJobPosting().getEmployer().getCompanyInformation().getDescription())
                        .fullName(app.getCandidate().getUser().getFullName())
                        .companyName(app.getJobPosting().getEmployer().getCompanyInformation().getCompanyName())
                        .logoUrl(app.getJobPosting().getEmployer().getCompanyInformation().getLogoUrl())
                        .location_company(app.getJobPosting().getEmployer().getCompanyInformation().getLocation())
                        .resumesId(app.getResume() != null ? app.getResume().getId() : null)
                        .resumeLink(app.getResumeLink())
                        .applicationStatus(app.getApplicationStatus())
                        .coverLetter(app.getCoverLetter())
                        .appliedAt(app.getAppliedAt())
                        .missingSkills(app.getMissingSkills() != null ? app.getMissingSkills() : List.of())
                        .minExperience(app.getMinExperience())
                        .experienceYears(app.getExperienceYears() != null ? app.getExperienceYears() : 0)
                        .skillMatchPercent(app.getSkillMatchPercent())
                        .isSkillQualified(app.getIsSkillQualified())
                        .isExperienceQualified(app.getIsExperienceQualified())
                        .skillMatchMessage(app.getSkillMatchMessage())
                        .build()
                )
                .collect(Collectors.toList());


//        // lấy thống kê theo trạng thái
//        List<Object[]> rawStats = applicantRepository.countApplicantsByStatus(jobId);
//        Map<String, Long> stats = rawStats.stream()
//                .collect(Collectors.toMap(
//                        row -> ((ApplicationStatus) row[0]).name(),
//                        row -> (Long) row[1]
//                ));
        // 2. Lấy thống kê từ DB (group by status)
        List<Object[]> rawStats = applicantRepository.countApplicantsByStatus(jobId);

        // 3. Khởi tạo map stats đầy đủ các trạng thái với mặc định = 0
        Map<String, Long> stats = new HashMap<>();
        for (ApplicationStatus s : ApplicationStatus.values()) {
            stats.put(s.name(), 0L);
        }

        // 4. Ghi đè lại những trạng thái có dữ liệu thật
        for (Object[] row : rawStats) {
            ApplicationStatus s = (ApplicationStatus) row[0];
            Long count = (Long) row[1];
            stats.put(s.name(), count);
        }
        // ✅ thêm ALL = tổng tất cả
        long total = stats.values().stream().mapToLong(Long::longValue).sum();
        stats.put("ALL", total);
        ApplicantsWithStatsDto result = ApplicantsWithStatsDto.builder()
                .applicants(applicants)
                .stats(stats)
                .build();

        return RestResponse.<ApplicantsWithStatsDto>builder()
                .statusCode(200)
                .error(null)
                .message("Success")
                .data(result)
                .build();
    }
    // EmployeeBrowseStatusService.java
    @Transactional
    public void markApplicantsAsRead(Long jobId, Long employerId) {
        // kiểm tra job thuộc về employer
        JobPosting job = jobPostingRepository.findByIdAndEmployer_Id(jobId, employerId)
                .orElseThrow(() -> new RuntimeException("Job not found or not yours"));

        // update all new applicants → set isNew = false
        applicantRepository.markAllAsReadByJob(jobId);
    }

}
