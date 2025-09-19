package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.EmployeeJobCompanyDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedEmployeeListJobResponseDto;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
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
                .build();
    }


    public PaginatedEmployeeListJobResponseDto getEmployerJobs(int page, int size, String sortBy, String sortDir) {
        // chỉ cho phép sort theo các field có trong JobPosting
        List<String> allowedSortFields = List.of("createdAt", "title", "status", "salaryRange");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt"; // fallback mặc định
        }
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Long employerId = authService.getCurrentUserEmployerId();

        Page<JobPosting> jobPostingPage = jobPostingRepository.findByEmployer_Id(employerId, pageable);
        List<JobPostingResponseDTO> jobDtos = jobPostingPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedEmployeeListJobResponseDto.builder()
                .data(jobDtos)
                .pageNumber(jobPostingPage.getNumber())
                .pageSize(jobPostingPage.getSize())
                .totalRecords(jobPostingPage.getTotalElements())
                .totalPages(jobPostingPage.getTotalPages())
                .hasNext(jobPostingPage.hasNext())
                .hasPrevious(jobPostingPage.hasPrevious())
                .build();
    }
    // ✅ 2. Employer xem danh sách applicant trong 1 job cụ thể
    public List<ApplicantResponseDto> getApplicantsByJob(Long jobId) {
        Long employerId = authService.getCurrentUserEmployerId();
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        if (!jobPosting.getEmployer().getId().equals(employerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xem job này");
        }

        return applicantRepository.findByJobPostingId(jobId).stream()
                .map(app -> ApplicantResponseDto.builder()
                        .id(app.getId())
                        .jobId(app.getJobPosting().getId())
                        .candidateId(app.getCandidate().getId())
                        .jobTitle(app.getJobPosting().getTitle())
                        .description_company(app.getJobPosting().getEmployer().getCompanyInformation().getDescription())
//                        .fullName(app.getResume() != null ? app.getResume().getFullName() : null)
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
                        .experienceYears(app.getExperienceYears() != null ? app.getExperienceYears() : 0) // ✅ tránh null
                        .skillMatchPercent(app.getSkillMatchPercent())        // ✅ map field mới
                        .isSkillQualified(app.getIsSkillQualified())          // ✅ map field mới
                        .isExperienceQualified(app.getIsExperienceQualified())
                        .skillMatchMessage(app.getSkillMatchMessage())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
