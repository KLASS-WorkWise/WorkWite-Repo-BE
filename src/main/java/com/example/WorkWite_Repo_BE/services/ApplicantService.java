package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.*;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
    private final ApplicantHistoryRepository applicantHistoryRepository;
    private final SseService sseService;

    private final FirebaseStorageService firebaseStorageService;


    // ApplicantService.java
    @Transactional
    public ApplicantResponseDto updateApplicantStatus(Long applicantId, ApplicationStatus newStatus, String note) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        applicant.setApplicationStatus(newStatus);
        applicantRepository.save(applicant);

        logHistory(applicant, newStatus, note);

        ApplicantResponseDto dto = convertToDto(applicant);

        // Push realtime SSE cho ứng viên
        sseService.sendEvent(applicantId, "statusUpdated", dto);

        return dto;
    }

    // ApplicantService.java
    private void logHistory(Applicant applicant, ApplicationStatus status, String note) {
        String changedBy = String.valueOf(authService.getCurrentUserFullName());

        ApplicantHistory history = ApplicantHistory.builder()
                .applicant(applicant)
                .status(status)
                .note(note)
                .changedAt(LocalDateTime.now())
                .changedBy(changedBy)   // ghi rõ ai thay đổi
                .build();

        applicantHistoryRepository.save(history);

    }


    public ApplicantResponseDto getApplicantDetail(Long applicantId) {
        Long currentCandidateId = authService.getCurrentUserCandidateId();

        Applicant app = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!app.getCandidate().getId().equals(currentCandidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }
        List<ApplicantHistory> historyList =
                applicantHistoryRepository.findByApplicantIdOrderByChangedAtAsc(app.getId());

        return ApplicantResponseDto.builder()
                .id(app.getId())
                .jobId(app.getJobPosting().getId())
                .candidateId(app.getCandidate().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .description_company(app.getJobPosting().getEmployer().getCompanyInformation().getDescription())
                .fullName(app.getResume() != null ? app.getResume().getFullName() : null)
                .companyName(app.getJobPosting().getEmployer().getCompanyInformation().getCompanyName())
                .logoUrl(app.getJobPosting().getEmployer().getCompanyInformation().getLogoUrl())

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
                .history(historyList.stream().map(h -> ApplicantHistoryDto.builder()
                        .status(h.getStatus())
                        .note(h.getNote())
                        .changedAt(h.getChangedAt())
                        .changedBy(h.getChangedBy())
                        .build()).toList())
                .build();

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

    private ApplicantResponseDto convertToDto(Applicant app) {
        return ApplicantResponseDto.builder()
                .id(app.getId())
                .jobId(app.getJobPosting().getId())
                .candidateId(app.getCandidate().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .description_company(app.getJobPosting().getEmployer().getCompanyInformation().getDescription())
                .fullName(app.getResume() != null ? app.getResume().getFullName() : null)
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
                .build();
    }



    // ✅ Tính skill còn thiếu
    private List<String> calculateMissingSkills(List<String> required, List<String> actual) {
        List<String> normRequired = normalizeSkillList(required);
        List<String> normActual = normalizeSkillList(actual);

        return normRequired.stream()
                .filter(req -> normActual.stream().noneMatch(act -> isSimilarSkill(req, act)))
                .toList();
    }

    // ✅ Levenshtein Distance cho phép typo nhỏ
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
    // Alias map: chuẩn hóa skill về dạng gốc
    private static final Map<String, String> SKILL_ALIASES = Map.ofEntries(
            Map.entry("js", "javascript"),
            Map.entry("javascript", "javascript"),
            Map.entry("nodejs", "nodejs"),
            Map.entry("node", "nodejs"),
            Map.entry("ts", "typescript"),
            Map.entry("typescript", "typescript"),
            Map.entry("reactjs", "react"),
            Map.entry("react", "react"),
            Map.entry("springboot", "spring"),
            Map.entry("spring boot", "spring"),
            Map.entry("sql", "sql"),
            Map.entry("mysql", "sql"),
            Map.entry("postgresql", "sql"),
            Map.entry("nosql", "nosql"),
            Map.entry("mongodb", "nosql")
            // 👉 bạn có thể mở rộng thêm tùy nhu cầu
    );

    // ✅ Kiểm tra 2 skill có giống nhau không (alias + typo nhỏ)
    private boolean isSimilarSkill(String skill1, String skill2) {
        if (skill1 == null || skill2 == null) return false;
        String s1 = SKILL_ALIASES.getOrDefault(normalizeSkill(skill1), normalizeSkill(skill1));
        String s2 = SKILL_ALIASES.getOrDefault(normalizeSkill(skill2), normalizeSkill(skill2));

        if (s1.isEmpty() || s2.isEmpty()) return false;
        if (s1.equals(s2)) return true;

        // Cho phép typo nhỏ (sai chính tả <= 1 ký tự)
        return levenshteinDistance(s1, s2) <= 1;
    }


    // ✅ Chuẩn hóa skill: lowercase + bỏ ký tự đặc biệt
    private String normalizeSkill(String skill) {
        if (skill == null) return "";
        return skill.toLowerCase()
                .replaceAll("[^a-z0-9]+", "") // giữ chữ + số
                .trim();
    }
    // ✅ Chuẩn hóa + map alias cho list skill
    private List<String> normalizeSkillList(List<String> skills) {
        if (skills == null) return List.of();
        return skills.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeSkill)
                .map(s -> SKILL_ALIASES.getOrDefault(s, s)) // alias map
                .distinct()
                .toList();
    }


    private long calculateExperienceYears(Resume resume) {
        if (resume.getExperiences() == null) return 0;
        int totalYears = 0;
        int totalMonths = 0;
        for (Experience exp : resume.getExperiences()) {
            if (exp.getStartYear() != null) {
                LocalDate end = exp.getEndYear() != null ? exp.getEndYear() : LocalDate.now();
                Period period = Period.between(exp.getStartYear(), end);

                totalYears += period.getYears();
                totalMonths += period.getMonths();
            }
        }

        // Làm tròn lên nếu >= 6 tháng
        if (totalMonths >= 6) totalYears += 1;

        return totalYears;
    }
    private String calculateExperienceDetail(Resume resume) {
        if (resume.getExperiences() == null || resume.getExperiences().isEmpty()) {
            return "Chưa có kinh nghiệm";
        }

        int totalYears = 0;
        int totalMonths = 0;

        for (Experience exp : resume.getExperiences()) {
            if (exp.getStartYear() != null) {
                LocalDate end = exp.getEndYear() != null ? exp.getEndYear() : LocalDate.now();
                Period period = Period.between(exp.getStartYear(), end);

                totalYears += period.getYears();
                totalMonths += period.getMonths();
            }
        }

        // Quy đổi số tháng dư thành năm
        totalYears += totalMonths / 12;
        totalMonths = totalMonths % 12;

        if (totalYears == 0 && totalMonths == 0) {
            return "Chưa có kinh nghiệm";
        } else if (totalYears == 0) {
            return totalMonths + " tháng";
        } else if (totalMonths == 0) {
            return totalYears + " năm";
        } else {
            return totalYears + " năm " + totalMonths + " tháng";
        }
    }
    // ✅ Tính % skill match
    private double calculateSkillMatchPercent(List<String> required, List<String> actual) {
        List<String> normRequired = normalizeSkillList(required);
        List<String> normActual = normalizeSkillList(actual);

        if (normRequired.isEmpty()) return 100.0; // không yêu cầu kỹ năng
        if (normActual.isEmpty()) return 0.0;     // ứng viên không có kỹ năng nào

        List<String> missing = calculateMissingSkills(normRequired, normActual);
        int matched = normRequired.size() - missing.size();
        return ((double) matched / normRequired.size()) * 100.0;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only select resume or upload file, not at the same time");
        }
        //  Check nhanh trước (tránh user apply nhiều lần liên tiếp)

        if (applicantRepository.existsByJobPostingIdAndCandidateId(jobId, candidateId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already applied for this job.");
        }

        Resume resume = null;
        String resumeLink = null;
        List<String> missingSkills = new ArrayList<>();
        String minExperienceMessage = null;
        long totalExpYears = 0;
        double skillMatchPercent = 0.0;
        boolean skillQualified =true;
        boolean expQualified = true;
        String skillMatchMessage = null;
        double requiredSkillPercent;


        if (applicantRequestDto.getResumesId() != null) {
            resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));

            if (!resume.getCandidate().getId().equals(candidateId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
            }

            resumeLink = resume.getResumeLink();

            // Tính toán kỹ năng còn thiếu + % match

            missingSkills = calculateMissingSkills(jobPosting.getRequiredSkills(), resume.getSkillsResumes());
            skillMatchPercent = calculateSkillMatchPercent(jobPosting.getRequiredSkills(), resume.getSkillsResumes());
            skillQualified = skillMatchPercent >=
                    Optional.ofNullable(jobPosting.getMinSkillMatchPercent()).orElse(30.0);
            requiredSkillPercent = Optional.ofNullable(jobPosting.getMinSkillMatchPercent()).orElse(30.0);


            if (!skillQualified) {
                skillMatchMessage = String.format("You only have %.1f%% skill match, minimum requirement %.1f%%",
                        skillMatchPercent, requiredSkillPercent);
            } else {
                skillMatchMessage = String.format("You have %.1f%% skill match, minimum requirement %.1f%%",
                        skillMatchPercent, requiredSkillPercent);
            }


            // Tính kinh nghiệm
            totalExpYears = calculateExperienceYears(resume);
            String expDetail = calculateExperienceDetail(resume);
            expQualified = totalExpYears >= jobPosting.getMinExperience();

            if (totalExpYears == 0) {
                minExperienceMessage = "Bạn chưa nhập kinh nghiệm hoặc chưa có kinh nghiệm (" + expDetail + ")";
            } else if (!expQualified) {
                minExperienceMessage = "Bạn chưa đủ " + jobPosting.getMinExperience() + " năm kinh nghiệm yêu cầu (hiện tại: " + expDetail + ")";
            } else {
                minExperienceMessage = "Bạn đủ yêu cầu kinh nghiệm (" + expDetail + ")";
            }

        }

        MultipartFile file = applicantRequestDto.getResumeFile();
        if (file != null && !file.isEmpty()) {
            validateFile(file);
//            resumeLink = saveResumeFile(file);
            resumeLink = firebaseStorageService.uploadFile(file);
        }

        if (resume == null && (resumeLink == null || resumeLink.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to choose resume or upload file");
        }

        Applicant applicant = Applicant.builder()
                .resume(resume)
                .resumeLink(resumeLink)
                .coverLetter(applicantRequestDto.getCoverLetter())
                .applicationStatus(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .jobPosting(jobPosting)
                .candidate(candidate)
                .missingSkills(missingSkills)
                .minExperience(minExperienceMessage)
                .experienceYears((int) totalExpYears)
                .skillMatchPercent(skillMatchPercent)    // ✅ lưu % skill match
                .isSkillQualified(skillQualified)        // ✅ lưu trạng thái skill
                .isExperienceQualified(expQualified)     // ✅ lưu trạng thái exp
                .skillMatchMessage(skillMatchMessage)
                .build();

        try {
            applicantRepository.save(applicant);
            logHistory(applicant, ApplicationStatus.PENDING, "Ứng viên vừa apply job");
        } catch (DataIntegrityViolationException ex) {
            // Race condition: DB unique constraint bắt duplicate
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already applied for this Job");
        }
//        applicantRepository.save(applicant);

        log.info("Ứng viên {} apply thành công vào job {} (match skill: {}%, exp {} năm)",
                candidateId, jobId, skillMatchPercent, totalExpYears);

        // Gửi notification (mock)
        log.info("Gửi thông báo tới Employer {}: Ứng viên {} vừa apply job {}", jobPosting.getEmployer().getId(), candidateId, jobId);

        return convertToDto(applicant);
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


    @Transactional
    public void deleteApplicant(Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        deleteResume(applicant.getResumeLink());
        applicantRepository.delete(applicant);
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (!(contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ chấp nhận PDF, DOC, DOCX");
        }
    }

//    // Upload hoặc apply job
//    public String handleResumeFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) return null;
//        validateFile(file);
//        return firebaseStorageService.uploadFile(file); // trả về filename
//    }

    // Download resume
    public Resource getResumeResource(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename trống");
        }
        return firebaseStorageService.downloadFile(filename);
    }

    // ApplicantService.java
    public void deleteResume(String resumeUrl) {
        if (resumeUrl == null || resumeUrl.isEmpty()) return;

        try {
            // Trích filename từ URL
            String filename = resumeUrl.substring(resumeUrl.lastIndexOf("/o/") + 3);
            filename = filename.split("\\?")[0]; // chỉ lấy tên file

            firebaseStorageService.deleteFile(filename);
            log.info("Đã xóa file resume: {}", filename);
        } catch (Exception e) {
            log.error("Không thể xóa resume trong Firebase: {}", resumeUrl, e);
        }
    }



//    public ApplicantResponseDto getApplicantDetail(Long applicantId) {
//        Long currentCandidateId = authService.getCurrentUserCandidateId();
//        Applicant applicant = applicantRepository.findById(applicantId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));
//
//        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
//        }
//        return convertToDto(applicant);
//    }

//    @Transactional
//    public void deleteApplicant(Long applicantId) {
//        Long currentCandidateId = authService.getCurrentUserCandidateId();
//        Applicant applicant = applicantRepository.findById(applicantId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));
//
//        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
//        }
//
//        String resumeLink = applicant.getResumeLink();
//        if (resumeLink != null && !resumeLink.isEmpty()) {
//            Path filePath = Paths.get(RESUME_UPLOAD_DIR).resolve(resumeLink).normalize();
//            try {
//                Files.deleteIfExists(filePath);
//                log.info("Đã xóa file resume: {}", resumeLink);
//            } catch (IOException e) {
//                log.error("Không xóa được file resume {}", resumeLink, e);
//            }
//        }
//        applicantRepository.delete(applicant);
//        log.info("Ứng viên {} đã xóa applicant {}", currentCandidateId, applicantId);
//    }
}
