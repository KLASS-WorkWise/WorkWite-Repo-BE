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
                .resumesId(applicant.getResume().getId())
                .resumeLink(applicant.getResumeLink())
                .applicationStatus(applicant.getApplicationStatus())
                .coverLetter(applicant.getCoverLetter())
                .appliedAt(applicant.getAppliedAt())
                .build();

    }

    public ApplicantResponseDto applyJob(Long jobId, @Valid ApplicantRequestDto applicantRequestDto) {
        // Lấy candidateId từ token
        Long candidateId = authService.getCurrentUserCandidateId();

        // 1. Lấy Candidate
        Candidate candidate = candidateJpaRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));

        // 2. Tìm job
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job không tồn tại"));

        // 3. Lấy resume
        Resume resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));

        // 4. Check resume thuộc về candidate
        if (!resume.getCandidate().getId().equals(candidateId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
        }

        // 5. Check đã apply chưa
        if (applicantRepository.existsByJobPosting_IdAndCandidate_Id(jobId, candidateId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã ứng tuyển công việc này rồi");
        }

        // Lấy danh sách applicants hiện tại
        List<Applicant> applicants = jobPosting.getApplicants();
        if (applicants == null) applicants = new ArrayList<>();
        // Kiểm tra skill
        List<String> requiredSkills = jobPosting.getRequiredSkills() != null ? jobPosting.getRequiredSkills() : new ArrayList<>();
        List<String> resumeSkills = resume.getSkillsResumes() != null ? resume.getSkillsResumes() : new ArrayList<>();
        List<String> missingSkills = requiredSkills.stream()
                .filter(req -> resumeSkills.stream().noneMatch(cv -> cv.equalsIgnoreCase(req)))
                .toList();
        // 4. Tính % match
        long matchedSkillsCount = requiredSkills.size() - missingSkills.size();
        double matchPercent = requiredSkills.isEmpty() ? 100.0 :
                ((double) matchedSkillsCount / requiredSkills.size()) * 100;

        // 5. Kiểm tra ngưỡng match, ví dụ >= 70%
        double threshold = 50.0;
        if (!missingSkills.isEmpty() && matchPercent < threshold) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bạn thiếu các kỹ năng bắt buộc: " + String.join(", ", missingSkills) +
                            ". Tỷ lệ kỹ năng đạt được: " + String.format("%.1f", matchPercent) + "% (ngưỡng " + threshold + "%)");
        }
//        // Thông báo kỹ năng
//        String skillStatus;
//        if (requiredSkills.isEmpty()) {
//            skillStatus = "Công việc này không yêu cầu kỹ năng cụ thể.";
//        } else if (missingSkills.isEmpty()) {
//            skillStatus = "Bạn đã đủ tất cả kỹ năng yêu cầu.";
//        } else {
//            skillStatus = "Thiếu các kỹ năng: " + String.join(", ", missingSkills);
//        }
        // Tính experience warning
        long totalExpYears = resume.getExperiences() != null
                ? resume.getExperiences().stream()
                .mapToLong(exp -> {
                    if (exp.getStartYear() != null && exp.getEndYear() != null) {
                        return exp.getEndYear() - exp.getStartYear();
                    }
                    return 0;
                }).sum()
                : 0;

        String experienceWarning = null;
        if (jobPosting.getMinExperience() == null || totalExpYears == 0) {
            experienceWarning = "Bạn chưa nhập kinh nghiệm hoặc chưa có kinh nghiệm";
        } else if (totalExpYears < jobPosting.getMinExperience()) {
            experienceWarning = "Bạn chưa đủ " + jobPosting.getMinExperience() + " năm kinh nghiệm yêu cầu";
        } else {
            experienceWarning = "Bạn đủ " + jobPosting.getMinExperience() + " năm kinh nghiệm yêu cầu";
        }



        System.out.println("resume :"+totalExpYears+"   job:"+ jobPosting.getMinExperience());



//            // 1. Lấy danh sách skill yêu cầu của Job
//                     List<String> requiredSkills = job.getSkillsRequired() != null ? job.getSkillsRequired() : new ArrayList<>();
//
//            // 2. Lấy danh sách skill từ Resume
//                    List<String> resumeSkills = resume.getSkillsResumes() != null ? resume.getSkillsResumes() : new ArrayList<>();
//
//            // 3. Liệt kê skill thiếu
//                    List<String> missingSkills = requiredSkills.stream()
//                            .filter(reqSkill -> resumeSkills.stream()
//                                    .noneMatch(cvSkill -> cvSkill.equalsIgnoreCase(reqSkill)))
//                            .toList();
//
//            // 4. Tính % match
//                    long matchedSkillsCount = requiredSkills.size() - missingSkills.size();
//                    double matchPercent = requiredSkills.isEmpty() ? 100.0 :
//                            ((double) matchedSkillsCount / requiredSkills.size()) * 100;
//
//            // 5. Kiểm tra ngưỡng match, ví dụ >= 70%
//                    double threshold = 70.0;
//                    if (!missingSkills.isEmpty() && matchPercent < threshold) {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                                "Bạn thiếu các kỹ năng bắt buộc: " + String.join(", ", missingSkills) +
//                                        ". Tỷ lệ kỹ năng đạt được: " + String.format("%.1f", matchPercent) + "% (ngưỡng " + threshold + "%)");
//                    }




        // Tạo Applicant mới
        // 7. Tạo applicant mới
        Applicant applicant = Applicant.builder()
//                .resumeLink(resume.getResumeLink()) // Lấy link từ DB luôn
                .resumeLink(applicantRequestDto.getResumeLink())
                .coverLetter(applicantRequestDto.getCoverLetter())
                .applicationStatus(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .jobPosting(jobPosting)
                .resume(resume)
                .candidate(candidate)
                .build();
        // Nếu bạn vẫn muốn lưu resumeId thì set thêm
//        if (request.getResumesId() != null) {
//            Resume resume = new Resume();
//            resume.setId(request.getResumesId());
//            applicant.setResume(resume);
//        }
        applicantRepository.save(applicant);

        return ApplicantResponseDto.builder()
                .id(applicant.getId())
                .jobId(jobPosting.getId())
                .candidateId(candidate.getId())
                .resumesId(resume.getId())
                .resumeLink(applicant.getResumeLink())
                .applicationStatus(applicant.getApplicationStatus())
                .coverLetter(applicant.getCoverLetter())
                .appliedAt(applicant.getAppliedAt())
                .missingSkills(missingSkills)
                .experienceWarning(experienceWarning)
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

        Pageable pageable = PageRequest.of(page, size);
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
