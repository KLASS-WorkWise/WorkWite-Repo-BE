package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final JobPostingRepository jobPostingRepository;
    private final CandidateJpaRepository candidateJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;
    private final ApplicantRepository applicantRepository;
    private final AuthService authService;



    private ApplicantResponseDto convertToDto(Applicant applicant) {
        return ApplicantResponseDto.builder()
                .id(applicant.getId())
                .jobId(applicant.getJobPosting().getId())
                .candidateId(applicant.getCandidate().getId())
                .resumesId(applicant.getResume() != null ? applicant.getResume().getId() : null)
                .resumeLink(applicant.getResumeLink())
                .applicationStatus(applicant.getApplicationStatus())
                .coverLetter(applicant.getCoverLetter())
                .appliedAt(applicant.getAppliedAt())
                .build();
    }


    public ApplicantResponseDto applyJob(Long jobId, @Valid ApplicantRequestDto applicantRequestDto) {
        Long candidateId = authService.getCurrentUserCandidateId();

        Candidate candidate = candidateJpaRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));

        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job không tồn tại"));

        Resume resume = null;
        String resumeLink = null;
        List<String> missingSkills = new ArrayList<>();
        String experienceWarning = null;
        List<String> requiredSkills = jobPosting.getRequiredSkills() != null ? jobPosting.getRequiredSkills() : new ArrayList<>();

        // --- 1. Nếu chọn resumeId ---
        if (applicantRequestDto.getResumesId() != null) {
            resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));

            if (!resume.getCandidate().getId().equals(candidateId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
            }

            // Gán resumeLink an toàn
            resumeLink = resume.getResumeLink() != null ? resume.getResumeLink() : null;

            // Kiểm tra kỹ năng từ resume trong DB
            List<String> resumeSkills = resume.getSkillsResumes() != null ? resume.getSkillsResumes() : new ArrayList<>();
            missingSkills = requiredSkills.stream()
                    .filter(skill -> resumeSkills.stream().noneMatch(cv -> cv.equalsIgnoreCase(skill)))
                    .toList();

            long matchedSkillsCount = requiredSkills.size() - missingSkills.size();
            double matchPercent = requiredSkills.isEmpty() ? 100.0 :
                    ((double) matchedSkillsCount / requiredSkills.size()) * 100;
            double threshold = 50.0;
            if (!missingSkills.isEmpty() && matchPercent < threshold) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Thiếu kỹ năng: " + String.join(", ", missingSkills) +
                                ". Tỷ lệ match: " + String.format("%.1f", matchPercent) + "% (yêu cầu ≥ " + threshold + "%)");
            }

            // Kiểm tra kinh nghiệm
            long totalExpYears = resume.getExperiences() != null
                    ? resume.getExperiences().stream()
                    .mapToLong(exp -> (exp.getStartYear() != null && exp.getEndYear() != null)
                            ? exp.getEndYear() - exp.getStartYear()
                            : 0).sum()
                    : 0;

            if (totalExpYears == 0) experienceWarning = "Bạn chưa nhập kinh nghiệm hoặc chưa có kinh nghiệm";
            else if (totalExpYears < jobPosting.getMinExperience())
                experienceWarning = "Bạn chưa đủ " + jobPosting.getMinExperience() + " năm kinh nghiệm yêu cầu";
            else experienceWarning = "Bạn đủ yêu cầu kinh nghiệm";
        }

        // --- 2. Nếu upload file mới ---
        // --- Nếu upload file ---
        if (applicantRequestDto.getResumeFile() != null && !applicantRequestDto.getResumeFile().isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/resumes/";
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                String fileName = System.currentTimeMillis() + "_" + applicantRequestDto.getResumeFile().getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                applicantRequestDto.getResumeFile().transferTo(filePath.toFile());

                resumeLink = "/files/resumes/" + fileName;

            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi upload hoặc đọc file CV");
            }
        }
        // --- 3. Nếu không có resumeId và không upload file ---
        if (resume == null && (resumeLink == null || resumeLink.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn cần chọn resume hoặc upload file");
        }

        // --- 4. Check đã apply chưa ---
        if (applicantRepository.existsByJobPostingIdAndCandidateId(jobId, candidateId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã ứng tuyển công việc này rồi");
        }

        // --- 5. Tạo Applicant ---
        Applicant applicant = Applicant.builder()
                .resume(resume)
                .resumeLink(resumeLink)
                .coverLetter(applicantRequestDto.getCoverLetter())
                .applicationStatus(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .jobPosting(jobPosting)
                .candidate(candidate)
                .build();

        applicantRepository.save(applicant);

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
                .minExperience(experienceWarning)
                .build();
    }


//    public List<ApplicantResponseDto> getApplicantsByCurrentUser() {
//        Long currentCandidateId = authService.getCurrentUserCandidateId();
//        List<Applicant> applicants = applicantRepository.findByCandidateId(currentCandidateId);
//        return applicants
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

    public PaginatedAppResponseDto getAllAppsByPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size,sort);
        Long currentCandidateId = authService.getCurrentUserCandidateId();
        Page<Applicant> applicants = this.applicantRepository.findByCandidateId(currentCandidateId ,pageable);
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

    public void deleteApplicant(Long applicantId) {
        Long currentCandidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }
        applicantRepository.delete(applicant);
    }
}
