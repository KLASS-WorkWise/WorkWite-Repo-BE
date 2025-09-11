package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ListApplicantResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantService {

    private final JobPostingRepository jobPostingRepository;
    private final CandidateJpaRepository candidateJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;
    private final ApplicantRepository applicantRepository;
    private final AuthService authService;

    @Value("${storage.resume-dir}")
    private String RESUME_UPLOAD_DIR;

    public Resource getResumeResource(String filename) {
        try {
            Path filePath = Paths.get(RESUME_UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File không tồn tại");
            }

            return resource;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải file");
        }
    }

    public String getContentType(String filename) {
        String ext = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) ext = filename.substring(i + 1).toLowerCase();

        switch (ext) {
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default: return "application/octet-stream";
        }
    }

    public boolean isPreviewable(String filename) {
        return filename.toLowerCase().endsWith(".pdf");
    }
//    public static final String RESUME_UPLOAD_DIR =  System.getProperty("user.dir") +"/uploads/resumes/";
//    public String getResumeUploadDir() {
//        return RESUME_UPLOAD_DIR;
//
//    }
    private ApplicantResponseDto convertToDto(Applicant app) {
        return ApplicantResponseDto.builder()
                .id(app.getId())
                .jobId(app.getJobPosting().getId())
                .candidateId(app.getCandidate().getId())
                .resumesId(app.getResume() != null ? app.getResume().getId() : null)
                .resumeLink(app.getResumeLink())
                .applicationStatus(app.getApplicationStatus())
                .coverLetter(app.getCoverLetter())
                .appliedAt(app.getAppliedAt())
                .build();
    }

    private void validateFile(MultipartFile file) {
        String ct = file.getContentType();
        if (!List.of(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ).contains(ct)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ upload PDF/DOC/DOCX");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không quá 5MB");
        }
    }

    private String saveResumeFile(MultipartFile file) {
        try {
            Path path = Paths.get(RESUME_UPLOAD_DIR);
            Files.createDirectories(path);

            String filename = System.currentTimeMillis() + "_" +
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            Path filePath = path.resolve(filename).normalize();

            file.transferTo(filePath.toFile());
            log.info("Upload resume thành công: {}", filename);

            return filename;
        } catch (IOException e) {
            log.error("Lỗi upload file resume", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi upload file");
        }
    }

    private List<String> calculateMissingSkills(List<String> required, List<String> actual) {
        if (required == null) return new ArrayList<>();
        if (actual == null) actual = new ArrayList<>();

        List<String> finalActual = actual;
        return required.stream()
                .filter(req -> finalActual.stream().noneMatch(cv -> isSimilarSkill(req, cv)))
                .toList();
    }

    private boolean isSimilarSkill(String a, String b) {
        return a.equalsIgnoreCase(b)
                || a.toLowerCase().contains(b.toLowerCase())
                || b.toLowerCase().contains(a.toLowerCase());
    }

    private long calculateExperienceYears(Resume resume) {
        if (resume.getExperiences() == null) return 0;

        return resume.getExperiences().stream()
                .mapToLong(exp -> {
                    if (exp.getStartYear() != null && exp.getEndYear() != null
                            && !exp.getEndYear().isBefore(exp.getStartYear())) {
                        return ChronoUnit.YEARS.between(exp.getStartYear(), exp.getEndYear());
                    }
                    return 0;
                })
                .sum();
    }

    @Transactional
    public ApplicantResponseDto applyJob(Long jobId, @Valid ApplicantRequestDto applicantRequestDto) {
        Long candidateId = authService.getCurrentUserCandidateId();

        Candidate candidate = candidateJpaRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found"));

        if (applicantRequestDto.getResumesId() != null &&
                applicantRequestDto.getResumeFile() != null &&
                !applicantRequestDto.getResumeFile().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được chọn resume hoặc upload file, không được cùng lúc");
        }

        if (applicantRepository.existsByJobPostingIdAndCandidateId(jobId, candidateId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã ứng tuyển công việc này rồi");
        }

        Resume resume = null;
        String resumeLink = null;
        List<String> missingSkills = new ArrayList<>();
        String minExperience = null;

        if (applicantRequestDto.getResumesId() != null) {
            resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));

            if (!resume.getCandidate().getId().equals(candidateId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
            }

            resumeLink = resume.getResumeLink();

            missingSkills = calculateMissingSkills(jobPosting.getRequiredSkills(), resume.getSkillsResumes());

            long matchedSkillsCount = jobPosting.getRequiredSkills() == null ? 0 :
                    jobPosting.getRequiredSkills().size() - missingSkills.size();

            double matchPercent = jobPosting.getRequiredSkills() == null || jobPosting.getRequiredSkills().isEmpty()
                    ? 100.0
                    : ((double) matchedSkillsCount / jobPosting.getRequiredSkills().size()) * 100;

            if (!missingSkills.isEmpty() && matchPercent < 20.0) {
                log.warn("Ứng viên {} thiếu kỹ năng quan trọng khi apply job {}", candidateId, jobId);
            }

            long totalExpYears = calculateExperienceYears(resume);
            if (totalExpYears == 0) {
                minExperience = "Bạn chưa nhập kinh nghiệm hoặc chưa có kinh nghiệm";
            } else if (totalExpYears < jobPosting.getMinExperience()) {
                minExperience = "Bạn chưa đủ " + jobPosting.getMinExperience() + " năm kinh nghiệm yêu cầu";
            } else {
                minExperience = "Bạn đủ yêu cầu kinh nghiệm";
            }
        }

        MultipartFile file = applicantRequestDto.getResumeFile();
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            resumeLink = saveResumeFile(file);
        }

        if (resume == null && (resumeLink == null || resumeLink.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn cần chọn resume hoặc upload file");
        }

        Applicant applicant = Applicant.builder()
                .resume(resume)
                .resumeLink(resumeLink)
                .coverLetter(applicantRequestDto.getCoverLetter())
                .applicationStatus(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .jobPosting(jobPosting)
                .candidate(candidate)
                .missingSkills(String.valueOf(missingSkills))
                .minExperience(minExperience)
                .build();

        applicantRepository.save(applicant);
        log.info("Ứng viên {} apply thành công vào job {}", candidateId, jobId);

        // Gửi notification (mock)
        log.info("Gửi thông báo tới Employer {}: Ứng viên {} vừa apply job {}", jobPosting.getEmployer().getId(), candidateId, jobId);

        return ApplicantResponseDto.builder()
                .id(applicant.getId())
                .jobId(jobPosting.getId())
                .candidateId(candidate.getId())
                .resumesId(resume != null ? resume.getId() : null)
                .resumeLink(resumeLink)
                .applicationStatus(ApplicationStatus.PENDING)
                .coverLetter(applicantRequestDto.getCoverLetter())
                .appliedAt(applicant.getAppliedAt())
                .missingSkills(missingSkills)
                .minExperience(minExperience)
                .build();
    }

    public PaginatedAppResponseDto getAllAppsByPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Long currentCandidateId = authService.getCurrentUserCandidateId();

        Page<Applicant> applicants = applicantRepository.findByCandidateId(currentCandidateId, pageable);
        List<ApplicantResponseDto> appDtos = applicants.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedAppResponseDto.builder()
                .data(appDtos)
                .pageNumber(applicants.getNumber())
                .pageSize(applicants.getSize())
                .totalRecords(applicants.getTotalElements())
                .totalPages(applicants.getTotalPages())
                .hasNext(applicants.hasNext())
                .hasPrevious(applicants.hasPrevious())
                .build();
    }

    public ApplicantResponseDto getApplicantDetail(Long applicantId) {
        Long currentCandidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }
        return convertToDto(applicant);
    }

    @Transactional
    public void deleteApplicant(Long applicantId) {
        Long currentCandidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }

        String resumeLink = applicant.getResumeLink();
        if (resumeLink != null && !resumeLink.isEmpty()) {
            Path filePath = Paths.get(RESUME_UPLOAD_DIR).resolve(resumeLink).normalize();
            try {
                Files.deleteIfExists(filePath);
                log.info("Đã xóa file resume: {}", resumeLink);
            } catch (IOException e) {
                log.error("Không xóa được file resume {}", resumeLink, e);
            }
        }
        applicantRepository.delete(applicant);
        log.info("Ứng viên {} đã xóa applicant {}", currentCandidateId, applicantId);
    }

    // hiển thị ai đã apply vào công ty

    public Page<ListApplicantResponseDTO> getApplicantsByEmployerAndPeriod(
            Long employerId,
            Long jobPostingId,
            ApplicationStatus status,
            String period,                  // "week", "month", hoặc null
            LocalDateTime customStartDate,  // cho custom filter
            LocalDateTime customEndDate,
            int page,
            int size
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;

        if ("week".equalsIgnoreCase(period)) {
            startDate = now.with(java.time.DayOfWeek.MONDAY)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            endDate = startDate.plusDays(6)
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        } else if ("month".equalsIgnoreCase(period)) {
            startDate = now.with(TemporalAdjusters.firstDayOfMonth())
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            endDate = now.with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        } else if ("custom".equalsIgnoreCase(period) && customStartDate != null && customEndDate != null) {
            startDate = customStartDate;
            endDate = customEndDate;
        } else {
            throw new IllegalArgumentException("Period must be 'week', 'month' or 'custom' with startDate & endDate");
        }

        Pageable pageable = PageRequest.of(page, size);

        return applicantRepository.findApplicantsByEmployerAndDateRange(
                employerId, jobPostingId, status, startDate, endDate, pageable
        );
    }

}
