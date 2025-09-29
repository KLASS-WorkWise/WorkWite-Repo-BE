package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.api.RestResponse;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingPaginatedDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.*;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.helpers.EmailTemplateHelper;
import com.example.WorkWite_Repo_BE.repositories.*;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
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
    private final ApplicantHistoryRepository applicantHistoryRepository;
    private final SseService sseService;

    private final FirebaseStorageService firebaseStorageService;
    private final ResumeParserService resumeParserService;
    private final EmailService emailService;
    private final EmailTemplateHelper emailTemplateHelper;
    private final InterviewScheduleRepository interviewScheduleRepository;


//    // ApplicantService.java
//    @Transactional
//    public ApplicantResponseDto updateApplicantStatus(Long applicantId, ApplicationStatus newStatus, String note) {
//        Long employerId = authService.getCurrentUserEmployerId();
//        Applicant applicant = applicantRepository.findById(applicantId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//
//        if (!applicant.getJobPosting().getEmployer().getId().equals(employerId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền cập nhật");
//        }
//
//        applicant.setApplicationStatus(newStatus);
//        applicantRepository.save(applicant);
//
//        logHistory(applicant, newStatus, note);
//
//        ApplicantResponseDto dto = convertToDto(applicant);
//
//        // Push realtime SSE cho ứng viên
//        sseService.sendEvent(applicantId, "statusUpdated", dto);
//
//// Gửi mail cho ứng viên
//        String candidateEmail = applicant.getCandidate().getUser().getEmail();
//        String candidateName = applicant.getResume() != null ? applicant.getResume().getFullName() : "Ứng viên";
//        String jobTitle = applicant.getJobPosting().getTitle();
//
//        String subject = "Cập nhật trạng thái đơn ứng tuyển";
//        String content = emailTemplateHelper.buildStatusUpdateEmail(candidateName, jobTitle, newStatus.name(), note, applicant.getId());
//        emailService.sendEmail(candidateEmail, subject, content);
//
//        return dto;
//    }

// ApplicantService.java
@Transactional
public ApplicantResponseDto updateApplicantStatus(Long applicantId, ApplicantStatusUpdateRequest request) {

    log.info("UpdateApplicantStatus request for applicantId={} request={}", applicantId, request);

    if (request == null || request.getStatus() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing status in request body");
    }
    Long employerId = authService.getCurrentUserEmployerId();
    Applicant applicant = applicantRepository.findById(applicantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!applicant.getJobPosting().getEmployer().getId().equals(employerId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền cập nhật");
    }

    ApplicationStatus currentStatus = applicant.getApplicationStatus();
    ApplicationStatus newStatus = request.getStatus();

    // === RÀNG BUỘC TRẠNG THÁI ===
    Map<ApplicationStatus, List<ApplicationStatus>> allowedNextStatus = Map.of(
            ApplicationStatus.PENDING, List.of(ApplicationStatus.CV_REVIEW),
            ApplicationStatus.CV_REVIEW, List.of(ApplicationStatus.INTERVIEW, ApplicationStatus.REJECTED),
            ApplicationStatus.INTERVIEW, List.of(ApplicationStatus.OFFER, ApplicationStatus.REJECTED),
            ApplicationStatus.OFFER, List.of(ApplicationStatus.HIRED, ApplicationStatus.REJECTED),
            ApplicationStatus.HIRED, List.of(),
            ApplicationStatus.REJECTED, List.of()
    );

    // Nếu đã HIRED hoặc REJECTED thì không được update
    if (currentStatus == ApplicationStatus.HIRED || currentStatus == ApplicationStatus.REJECTED) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cannot update status. Applicant is already " + currentStatus.name());
    }

    // Kiểm tra trạng thái hợp lệ
    List<ApplicationStatus> allowedNext = allowedNextStatus.getOrDefault(currentStatus, List.of());
    if (!allowedNext.contains(newStatus)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid status update from " + currentStatus.name() + " to " + newStatus.name());
    }

    // === CẬP NHẬT TRẠNG THÁI ===
    applicant.setApplicationStatus(newStatus);
    applicantRepository.save(applicant);

    // === Lưu lịch sử thay đổi ===
    logHistory(applicant, newStatus, request.getNote());

    ApplicantResponseDto dto = convertToDto(applicant);

    // Push realtime SSE cho ứng viên
    sseService.sendEvent(applicantId, "statusUpdated", dto);

    // Gửi mail cho ứng viên
    String candidateEmail = applicant.getCandidate().getUser().getEmail();
    String candidateName = applicant.getResume() != null ? applicant.getResume().getFullName() : "Ứng viên";
    String jobTitle = applicant.getJobPosting().getTitle();

    if (newStatus == ApplicationStatus.INTERVIEW) {
        // ✅ Lưu lịch phỏng vấn
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setApplicant(applicant);
        schedule.setScheduledAt(request.getScheduledAt());
        schedule.setLocation(request.getLocation());
        schedule.setInterviewer(request.getInterviewer());
        interviewScheduleRepository.save(schedule);

        // Gửi mail lịch phỏng vấn
        String subject = "Thư mời phỏng vấn cho vị trí " + jobTitle;
        String content = emailTemplateHelper.buildInterviewScheduleEmail(
                candidateName,
                jobTitle,
                schedule.getScheduledAt(),
                schedule.getLocation(),
                schedule.getInterviewer()
        );
        emailService.sendEmail(candidateEmail, subject, content);

    } else {
        // Mail update status bình thường
        String subject = "Cập nhật trạng thái đơn ứng tuyển";
        String content = emailTemplateHelper.buildStatusUpdateEmail(
                candidateName, jobTitle, newStatus.name(), request.getNote(), applicant.getId()
        );
        emailService.sendEmail(candidateEmail, subject, content);
    }

    return dto;
}

    // Timeline
    public List<ApplicantHistory> getTimeline(Long applicantId) {
        return applicantHistoryRepository.findByApplicantIdOrderByChangedAtAsc(applicantId);
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
//        // Lấy thông tin user hiện tại
//        Long currentCandidateId = authService.getCurrentUserCandidateId();
//        Long currentEmployerId = authService.getCurrentUserEmployerId();

        // Lấy applicant
        Applicant app = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

//        // Kiểm tra quyền truy cập
//        boolean canAccess = false;
//
//        if (currentCandidateId != null && app.getCandidate().getId().equals(currentCandidateId)) {
//            canAccess = true;
//        }
//
//        if (currentEmployerId != null && app.getJobPosting().getEmployer().getId().equals(currentEmployerId)) {
//            canAccess = true;
//        }
//
//        if (!canAccess) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được phép xem applicant này");
//        }

        List<ApplicantHistory> historyList =
                applicantHistoryRepository.findByApplicantIdOrderByChangedAtAsc(app.getId());

        return ApplicantResponseDto.builder()
                .id(app.getId())
                .jobId(app.getJobPosting().getId())
                .candidateId(app.getCandidate().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .description_company(app.getJobPosting().getEmployer().getCompanyInformation().getDescription())
//                .fullName(app.getResume() != null ? app.getResume().getFullName() : null)
                .fullName(app.getCandidate().getUser().getFullName())
                .companyName(app.getJobPosting().getEmployer().getCompanyInformation().getCompanyName())
                .logoUrl(app.getJobPosting().getEmployer().getCompanyInformation().getLogoUrl())
                .salaryRange(app.getJobPosting().getSalaryRange())
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
//                .history(historyList.stream().map(h -> ApplicantHistoryDto.builder()
//                        .status(h.getStatus())
//                        .note(h.getNote())
//                        .changedAt(h.getChangedAt())
//                        .changedBy(h.getChangedBy())
//                        .build()).toList())
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
                .salaryRange(app.getJobPosting().getSalaryRange())   // ✅ thêm dòng này
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
                .isRead(false)
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
    //chuẩn hóa học vấn
    private String normalizeDegree(String degree) {
        if (degree == null) return "";
        return degree.trim().toLowerCase()
                .replace("đại học", "bachelor")
                .replace("cử nhân", "bachelor")
                .replace("cao đẳng", "college")
                .replace("thạc sĩ", "master")
                .replace("tiến sĩ", "phd")
                .replace("associate", "associate")
                .replace("bachelor", "bachelor")
                .replace("college", "college")
                .replace("master", "master")
                .replace("phd", "phd");
    }
    private int degreeLevel(String normDegree) {
        return switch (normDegree) {
            case "associate" -> 1; // Trung cấp / Associate
            case "college"   -> 2; // Cao đẳng / College
            case "bachelor"  -> 3; // Đại học / Bachelor
            case "master"    -> 4; // Thạc sĩ / Master
            case "phd"       -> 5; // Tiến sĩ / PhD
            default -> 0;
        };
    }
    // Alias map: chuẩn hóa skill về dạng gốc
    private static final Map<String, String> SKILL_ALIASES = Map.ofEntries(
            // JavaScript ecosystem
            Map.entry("js", "javascript"),
            Map.entry("javascript", "javascript"),
            Map.entry("ecmascript", "javascript"),
            Map.entry("nodejs", "nodejs"),
            Map.entry("node", "nodejs"),
            Map.entry("expressjs", "nodejs"),
            Map.entry("ts", "typescript"),
            Map.entry("typescript", "typescript"),

            // React ecosystem
            Map.entry("reactjs", "react"),
            Map.entry("react", "react"),
            Map.entry("nextjs", "react"),
            Map.entry("redux", "react"),

            // Spring / Java
            Map.entry("springboot", "spring"),
            Map.entry("spring boot", "spring"),
            Map.entry("spring framework", "spring"),
            Map.entry("hibernate orm", "hibernate"),

            // SQL / Database
            Map.entry("sql", "sql"),
            Map.entry("mysql", "sql"),
            Map.entry("postgresql", "sql"),
            Map.entry("postgres", "sql"),
            Map.entry("mssql", "sql"),
            Map.entry("oracle", "sql"),
            Map.entry("sqlite", "sql"),
            Map.entry("nosql", "nosql"),
            Map.entry("mongodb", "nosql"),
            Map.entry("cassandra", "nosql"),
            Map.entry("dynamodb", "nosql"),

            // Cloud
            Map.entry("amazon web services", "aws"),
            Map.entry("aws", "aws"),
            Map.entry("azure cloud", "azure"),
            Map.entry("gcp", "gcp"),
            Map.entry("google cloud", "gcp"),

            // DevOps
            Map.entry("k8s", "kubernetes"),
            Map.entry("kubernetes", "kubernetes"),
            Map.entry("docker-compose", "docker"),
            Map.entry("ci/cd", "devops"),
            Map.entry("jenkins pipeline", "jenkins"),

            // Programming languages (aliases / abbreviations)
            Map.entry("c++", "c++"),
            Map.entry("cpp", "c++"),
            Map.entry("c#", "c#"),
            Map.entry("c sharp", "c#"),
            Map.entry("py", "python"),
            Map.entry("python", "python"),
            Map.entry("golang", "go"),
            Map.entry("go", "go"),
            Map.entry("jsf", "java"),
            Map.entry("jsp", "java"),

            // Mobile
            Map.entry("android sdk", "android"),
            Map.entry("ios", "ios"),
            Map.entry("swiftui", "swift"),
            Map.entry("objective-c", "objective-c"),
            Map.entry("rn", "react native"),
            Map.entry("react native", "react native"),

            // Tools
            Map.entry("gitlab ci", "gitlab ci"),
            Map.entry("github actions", "github actions"),
            Map.entry("jira software", "jira"),
            Map.entry("confluence wiki", "confluence")
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
            return "No experience";
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
            return "No experience";
        } else if (totalYears == 0) {
            return totalMonths + " month";
        } else if (totalMonths == 0) {
            return totalYears + " years";
        } else {
            return totalYears + " years " + totalMonths + " month";
        }
    }
    //  Tính % skill match
    private double calculateSkillMatchPercent(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 0.0;
        if (candidateSkills == null || candidateSkills.isEmpty()) return 0.0;

        long matched = candidateSkills.stream()
                .filter(c -> requiredSkills.stream()
                        .anyMatch(req -> req.equalsIgnoreCase(c)))
                .count();

        // lấy trung bình giữa tỉ lệ match so với job và so với ứng viên
        double percentByJob = (matched * 100.0) / requiredSkills.size();
        double percentByCandidate = (matched * 100.0) / candidateSkills.size();

        return (percentByJob + percentByCandidate) / 2.0;
    }
    // Tính kinh nghiệm
    public double calculateExperienceScore(double totalExpYears, double requiredExp) {
        if (totalExpYears <= 0) {
            return 0; // Không có kinh nghiệm
        }

        if (requiredExp <= 0) {
            return 100; // Job không yêu cầu kinh nghiệm
        }

        if (totalExpYears >= requiredExp) {
            return 100; // Đủ hoặc nhiều hơn yêu cầu
        }

        // Nếu ít hơn yêu cầu thì tính tỉ lệ %
        double ratio = (double) totalExpYears / requiredExp;
        return (int) Math.round(ratio * 100);
    }
    public double calculateEducationScore(String requiredDegree, List<String> candidateDegrees) {
        if (requiredDegree == null || requiredDegree.isBlank()) {
            return 100; // Không yêu cầu / No requirement
        }
        if (candidateDegrees == null || candidateDegrees.isEmpty()) {
            return 0; // Không có học vấn / No education info
        }

        String requiredNorm = normalizeDegree(requiredDegree);
        double requiredLevel = degreeLevel(requiredNorm);

        // Lấy mức học vấn cao nhất của ứng viên / Get candidate's highest degree
        double maxCandidateLevel = candidateDegrees.stream()
                .map(this::normalizeDegree)
                .mapToInt(this::degreeLevel)
                .max()
                .orElse(0);

        if (requiredLevel == 0) return 0;

        if (maxCandidateLevel >= requiredLevel) {
            return 100; // Đủ hoặc cao hơn yêu cầu / Equal or higher than required
        }

        // Tính tỷ lệ nếu thấp hơn yêu cầu / Scale proportionally if lower
        return (double) Math.round(((double) maxCandidateLevel / requiredLevel) * 100);
    }
    public double calculateTotalMatchWeighted(double skillScore, double expScore, double eduScore) {
        double skillWeight = 0.6; // 60%
        double expWeight   = 0.2; // 20%
        double eduWeight   = 0.2; // 20%

        return (skillScore * skillWeight) + (expScore * expWeight) + (eduScore * eduWeight);
    }

    @Transactional
    public RestResponse<ApplicantResponseDto> applyJob(Long jobId, @Valid ApplicantRequestDto applicantRequestDto) {
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
                minExperienceMessage = "You have not entered experience or have no experience (" + expDetail + ")";
            } else if (!expQualified) {
                minExperienceMessage = "You are not enough " + jobPosting.getMinExperience() + " Years of experience required (current: " + expDetail + ")";
            } else {
                minExperienceMessage = "You have the required experience (" + expDetail + ")";
            }

        }
        else if (applicantRequestDto.getResumeFile() != null && !applicantRequestDto.getResumeFile().isEmpty()) {
            MultipartFile file = applicantRequestDto.getResumeFile();
            if (file != null && !file.isEmpty()) {
                validateFile(file);
//            resumeLink = saveResumeFile(file);
                resumeLink = firebaseStorageService.uploadFile(file);
                String extractedText = resumeParserService.extractText(file);

                // 👉 Trích xuất skill
                List<String> extractedSkills = resumeParserService.extractSkills(extractedText);
                 totalExpYears = resumeParserService.extractExperienceYears(extractedText);

// 👉 check skill match
                missingSkills = calculateMissingSkills(jobPosting.getRequiredSkills(), extractedSkills);
                skillMatchPercent = calculateSkillMatchPercent(jobPosting.getRequiredSkills(), extractedSkills);
                requiredSkillPercent = Optional.ofNullable(jobPosting.getMinSkillMatchPercent()).orElse(30.0);
                skillQualified = skillMatchPercent >= requiredSkillPercent;
                skillMatchMessage = skillQualified
                        ? String.format("You have %.1f%% skill match (minimum requirement %.1f%%)", skillMatchPercent, requiredSkillPercent)
                        : String.format("You only have %.1f%% skill match (minimum requirement %.1f%%)", skillMatchPercent, requiredSkillPercent);

                // 👉 Trích xuất kinh nghiệm
                totalExpYears = resumeParserService.extractExperienceYears(extractedText);
                expQualified = totalExpYears >= jobPosting.getMinExperience();
                if (!expQualified) {
                    minExperienceMessage = "You do not have enough " + jobPosting.getMinExperience()
                            + " years of experience (current: " + totalExpYears + " years)";
                } else {
                    minExperienceMessage = "You have enough experience requirement (" + totalExpYears + " years)";
                }

            }
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
                .isRead(false)
                .build();

        try {
            applicantRepository.save(applicant);

            // Gửi mail cho ứng viên
            String candidateEmail = applicant.getCandidate().getUser().getEmail();
            String candidateName = applicant.getResume() != null ? applicant.getResume().getFullName() : "Ứng viên";
            String jobTitle = applicant.getJobPosting().getTitle();

            String subjectCandidate = "Xác nhận ứng tuyển thành công";
            String contentCandidate = emailTemplateHelper.buildApplySuccessEmail(candidateName, jobTitle, applicant.getId());
            emailService.sendEmail(candidateEmail, subjectCandidate, contentCandidate);

// Gửi mail cho Employer
            Employers employer = applicant.getJobPosting().getEmployer();
            String employerEmail = employer.getUser().getEmail();
            String employerName = employer.getUser().getFullName();

            String subjectEmployer = "Có ứng viên mới ứng tuyển vào công việc " + jobTitle;
            String contentEmployer = emailTemplateHelper.buildNewApplicantEmail(employerName, jobTitle, candidateName, applicant.getId());
            emailService.sendEmail(employerEmail, subjectEmployer, contentEmployer);

            logHistory(applicant, ApplicationStatus.PENDING, "Candidates who have just applied for the job");
        } catch (DataIntegrityViolationException ex) {
            // Race condition: DB unique constraint bắt duplicate
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already applied for this Job");
        }
//        applicantRepository.save(applicant);

        log.info("Ứng viên {} apply thành công vào job {} (match skill: {}%, exp {} năm)",
                candidateId, jobId, skillMatchPercent, totalExpYears);

        // Gửi notification (mock)
        log.info("Gửi thông báo tới Employer {}: Ứng viên {} vừa apply job {}", jobPosting.getEmployer().getId(), candidateId, jobId);

        ApplicantResponseDto dto = convertToDto(applicant);
        // ✅ Bọc response
        return RestResponse.<ApplicantResponseDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Ứng tuyển thành công")
                .data(dto)
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


    @Transactional
    public void deleteApplicant(Long applicantId) {
        Long candidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));
        if (!applicant.getCandidate().getId().equals(candidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa");
        }
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

// Tính tỷ lệ % matching cv
//@Transactional(readOnly = true)
//public RestResponse<PreviewResponseDto> previewJobApplication(Long jobId, @Valid ApplicantRequestDto applicantRequestDto) {
//    Long candidateId = authService.getCurrentUserCandidateId();
//
//    Candidate candidate = candidateJpaRepository.findById(candidateId)
//            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
//
//    JobPosting jobPosting = jobPostingRepository.findById(jobId)
//            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found"));
//
//    double skillMatchPercent = 0.0;
//
//    if (applicantRequestDto.getResumesId() != null) {
//        Resume resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));
//
//        if (!resume.getCandidate().getId().equals(candidateId)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
//        }
//
//        skillMatchPercent = calculateSkillMatchPercent(jobPosting.getRequiredSkills(), resume.getSkillsResumes());
//
//    } else if (applicantRequestDto.getResumeFile() != null && !applicantRequestDto.getResumeFile().isEmpty()) {
//        MultipartFile file = applicantRequestDto.getResumeFile();
//        validateFile(file);
//
//        String extractedText = resumeParserService.extractText(file);
//        List<String> extractedSkills = resumeParserService.extractSkills(extractedText);
//        log.info("Extracted text length: {}", extractedText.length());
//        log.info("Extracted text sample: {}", extractedText.substring(0, Math.min(500, extractedText.length())));
//        log.info("Uploaded file: {}, size: {}", file.getOriginalFilename(), file.getSize());
//        log.info("Job required skills: {}", jobPosting.getRequiredSkills());
//        log.info("Extracted skills from CV: {}", extractedSkills);
//
//        skillMatchPercent = calculateSkillMatchPercent(jobPosting.getRequiredSkills(), extractedSkills);
//    } else {
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn cần chọn Resume hoặc upload file");
//    }
//
//    PreviewResponseDto dto = PreviewResponseDto.builder()
//            .skillMatchPercent(skillMatchPercent)
//            .build();
//
//    return RestResponse.<PreviewResponseDto>builder()
//            .statusCode(HttpStatus.OK.value())
//            .message("Preview successful")
//            .data(dto)
//            .build();
//}

    @Transactional(readOnly = true)
    public PreviewResponseDto previewJobApplication(Long jobId,
                                                          @Valid ApplicantRequestDto applicantRequestDto) {
        Long candidateId = authService.getCurrentUserCandidateId();

        Candidate candidate = candidateJpaRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found"));

        double skillMatchPercent = 0.0;

        if (applicantRequestDto.getResumesId() != null) {
            Resume resume = resumeJpaRepository.findById(applicantRequestDto.getResumesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume không tồn tại"));

            if (!resume.getCandidate().getId().equals(candidateId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume không thuộc về tài khoản của bạn");
            }

            // ✅ Tính toán % match từ resume đã lưu
            double skillScore  = calculateSkillMatchPercent(
                    jobPosting.getRequiredSkills(),
                    resume.getSkillsResumes()
            );
            // ✅ Exp
            double totalExpYears = calculateExperienceYears(resume);
            double expScore = calculateExperienceScore(totalExpYears, jobPosting.getMinExperience());
            // ✅ Edu
            List<String> candidateEdu = resume.getEducations().stream().map(Education::getDegree).toList();   // ví dụ Bachelor = 3
            String requiredEdu  = jobPosting.getRequiredDegree();  // ví dụ Master = 4
            double eduScore = calculateEducationScore(requiredEdu, candidateEdu);

            skillMatchPercent = calculateTotalMatchWeighted(skillScore,expScore,eduScore);

            return PreviewResponseDto.builder()
                    .resumesId(resume.getId())
                    .skillMatchPercent(skillMatchPercent)
                    .build();

        } else if (applicantRequestDto.getResumeFile() != null
                && !applicantRequestDto.getResumeFile().isEmpty()) {

            MultipartFile file = applicantRequestDto.getResumeFile();
            validateFile(file);

            // ❌ Preview: không upload Firebase, chỉ phân tích text
            String extractedText = resumeParserService.extractText(file);
            List<String> extractedSkills = resumeParserService.extractSkills(extractedText);

            skillMatchPercent = calculateSkillMatchPercent(
                    jobPosting.getRequiredSkills(),
                    extractedSkills
            );

            return PreviewResponseDto.builder()
                    .resumesId(null)
                    .skillMatchPercent(skillMatchPercent)
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn phải chọn resumeId hoặc upload file");
    }


}
