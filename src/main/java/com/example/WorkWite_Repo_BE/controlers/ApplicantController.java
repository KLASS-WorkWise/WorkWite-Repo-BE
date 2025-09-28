package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.api.RestResponse;
import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.ApplicantHistory;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/applicant")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;
    private final AuthService authService;
    private final ApplicantHistoryService applicantHistoryService;
    private final ApplicantRepository applicantRepository;
    private final SseService sseService;
    private final FirebaseStorageService firebaseStorageService;
    private final RestTemplate restTemplate = new RestTemplate();
    // ApplicantController.java
    @GetMapping("/{id}/tracking")
    public ResponseEntity<ApplicantTrackingDto> getApplicantTracking(@PathVariable Long id) {
        Long candidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getCandidate().getId().equals(candidateId)) {
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
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<ApplicantHistory>> timeline(@PathVariable Long id) {
        return ResponseEntity.ok(applicantService.getTimeline(id));
    }
    // ApplicantController.java
    @GetMapping("/{id}/subscribe")
    public SseEmitter subscribe(@PathVariable Long id) {
        Long candidateId = authService.getCurrentUserCandidateId();
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!applicant.getCandidate().getId().equals(candidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }

        return sseService.registerEmitter(id);
    }

    @GetMapping("/{applicantId}/history")
    public List<ApplicantHistoryDto> getApplicantHistory(@PathVariable Long applicantId) {
        return applicantHistoryService.getHistory(applicantId);}
//    @PutMapping("/{id}/status")
//    public ResponseEntity<ApplicantResponseDto> updateApplicantStatus(
//            @PathVariable Long id,
//            @RequestBody ApplicantStatusUpdateRequest request
//    ) {
//        ApplicantResponseDto updated = applicantService.updateApplicantStatus(
//                id,
//                request.getStatus(),
//                request.getNote()
//        );
//        return ResponseEntity.ok(updated);
//    }

    @PostMapping(value = "/{jobId}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<ApplicantResponseDto>> applyJob(
            @PathVariable Long jobId,
            @ModelAttribute @Valid ApplicantRequestDto applicantRequestDto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicantService.applyJob(jobId, applicantRequestDto));
    }


    @GetMapping("")
    public PaginatedAppResponseDto getAllAppsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir){
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        return this.applicantService.getAllAppsByPage(page, size,sortBy, sortDir);
    }



    @GetMapping("/detail/{applicantId}")
    public ResponseEntity<ApplicantResponseDto> getApplicantDetail(@PathVariable Long applicantId) {
        return ResponseEntity.ok(applicantService.getApplicantDetail(applicantId));
    }

//    @DeleteMapping("/delete/{applicantId}")
//    public ResponseEntity<Void> deleteApplicant(@PathVariable Long applicantId) {
//        applicantService.deleteApplicant(applicantId);
//        return ResponseEntity.noContent().build();
//    }
//    @GetMapping("/resume-link/{filename}")
//    public ResponseEntity<Resource> getResumeLink(@PathVariable String filename) {
//        Resource resource = applicantService.getResumeResource(filename);
//        String contentType = applicantService.getContentType(filename);
//        boolean preview = applicantService.isPreviewable(filename);
//
//        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType));
//
//        if (preview) {
//            responseBuilder.header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"");
//        } else {
//            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");
//        }
//
//        return responseBuilder.body(resource);
//    }
@GetMapping("/resume-preview")
public ResponseEntity<byte[]> previewResume(@RequestParam String url) {
    try {
        // Lấy file từ Firebase
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // hoặc content-type từ response nếu cần
        headers.setContentDisposition(ContentDisposition.inline().filename("resume.pdf").build());

        return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }
}
    // Delete applicant
    @DeleteMapping("/delete/{applicantId}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable Long applicantId) {
        applicantService.deleteApplicant(applicantId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Lấy danh sách ứng viên theo tuần hoặc tháng
     * @param employerId id của employer
     * @param period "week" hoặc "month"
     */
    @GetMapping("/{employerId}/filter")
    public Page<ListApplicantResponseDTO> getApplicantsByPeriod(
            @PathVariable Long employerId,
            @RequestParam(required = false) Long jobPostingId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return applicantService.getApplicantsByEmployerAndPeriod(
                employerId,
                jobPostingId,
                status,
                period,
                startDate,
                endDate,
                page,
                size
        );

    }

    @PostMapping(
            value = "/{jobId}/preview",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<PreviewResponseDto> previewApplication(
            @PathVariable Long jobId,
            @Valid @ModelAttribute ApplicantRequestDto applicantRequestDto
    ) {
        PreviewResponseDto response = applicantService.previewJobApplication(jobId, applicantRequestDto);
        return ResponseEntity.ok(response);
    }

}
