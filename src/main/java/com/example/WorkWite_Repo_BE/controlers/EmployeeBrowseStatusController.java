package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("api/employers-status")
@CrossOrigin(origins = "http://localhost:5173`")

public class EmployeeBrowseStatusController {
    private final ApplicantService applicantService;
    private final AuthService authService;
    private final ApplicantHistoryService applicantHistoryService;
    private final ApplicantRepository applicantRepository;
    private final EmployeeBrowseStatusService employeeBrowseStatusService;

    public EmployeeBrowseStatusController(ApplicantService applicantService, AuthService authService, ApplicantHistoryService applicantHistoryService, ApplicantRepository applicantRepository, EmployeeBrowseStatusService employeeBrowseStatusService) {
        this.applicantService = applicantService;
        this.authService = authService;
        this.applicantHistoryService = applicantHistoryService;
        this.applicantRepository = applicantRepository;
        this.employeeBrowseStatusService = employeeBrowseStatusService;
    }
    @GetMapping("/{id}/tracking")
    public ResponseEntity<ApplicantTrackingDto> getApplicantTracking(@PathVariable Long id) {
        Long employeeId = authService.getCurrentUserEmployerId();
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getJobPosting().getEmployer().getId().equals(employeeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }

        ApplicantResponseDto detail = applicantService.getApplicantDetail(id);
        List<ApplicantHistoryDto> history = applicantHistoryService.getHistory(id);
        List<TimelineEventResponse> timeline = applicantHistoryService.getFullTimeline(applicant);

        ApplicantTrackingDto dto = ApplicantTrackingDto.builder()
                .detail(detail)
                .history(history)
                .timeline(timeline)
                .build();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/applicants/{jobId}")
    public ResponseEntity<List<ApplicantResponseDto>> list(@PathVariable Long jobId) {
        return ResponseEntity.ok(employeeBrowseStatusService.getApplicantsByJob(jobId));
    }

    @PutMapping("/applicants/{id}/status")
    public ResponseEntity<ApplicantResponseDto> updateStatus(@PathVariable Long id,
                                                             @RequestParam ApplicationStatus status,
                                                             @RequestParam(required = false) String note) {
        return ResponseEntity.ok(applicantService.updateApplicantStatus(id, status, note));
    }

    // ✅ Employer xem danh sách job của mình
    @GetMapping("/jobs")
    public PaginatedEmployeeListJobResponseDto getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return employeeBrowseStatusService.getEmployerJobs(page, size, sortBy, sortDir);
    }

    // ✅ Employer xem applicant trong job cụ thể
    @GetMapping("/jobs/{jobId}/applicants")
    public List<ApplicantResponseDto> getApplicantsByJob(@PathVariable Long jobId) {
        return employeeBrowseStatusService.getApplicantsByJob(jobId);
    }
}