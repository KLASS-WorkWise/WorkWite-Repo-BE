package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.services.ApplicantHistoryService;
import com.example.WorkWite_Repo_BE.services.ApplicantService;
import com.example.WorkWite_Repo_BE.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/applicant")
@Validated
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;
    private final AuthService authService;
    private final ApplicantHistoryService applicantHistoryService;
    private final ApplicantRepository applicantRepository;

    @GetMapping("/{applicantId}/history")
    public List<ApplicantHistoryDto> getApplicantHistory(@PathVariable Long applicantId) {
        return applicantHistoryService.getHistory(applicantId);}
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicantStatus(
            @PathVariable Long id,
            @RequestBody ApplicantStatusUpdateRequest request
    ) {
//        // 🚨 TODO: kiểm tra role HR/Admin (ví dụ thông qua AuthService)
//        String changedBy = "Users"; // Lấy từ AuthService thực tế

        Applicant updated = applicantService.updateApplicantStatus(id, request);

        return ResponseEntity.ok("Cập nhật trạng thái thành công: " + updated.getApplicationStatus());
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<ApplicantTimelineDto>> getApplicantTimeline(@PathVariable Long id) {
        Long currentCandidateId = authService.getCurrentUserCandidateId();

        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant không tồn tại"));

        if (!applicant.getCandidate().getId().equals(currentCandidateId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicant không thuộc về bạn");
        }

        return ResponseEntity.ok(applicantHistoryService.getFullTimeline(applicant));
    }

    @PostMapping(value = "/{jobId}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicantResponseDto> applyJob(
            @PathVariable  Long jobId,
            @ModelAttribute  @Valid ApplicantRequestDto applicantRequestDto) throws Exception {

        ApplicantResponseDto response = applicantService.applyJob(jobId, applicantRequestDto);
        System.out.println("ResumeFile: " + applicantRequestDto.getResumeFile());
        System.out.println("CoverLetter: " + applicantRequestDto.getCoverLetter());
        System.out.println("ResumesId: " + applicantRequestDto.getResumesId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }
    //    @GetMapping
//    public ResponseEntity<List<ApplicantResponseDto>> getMyApplicants() {
//        return ResponseEntity.ok(applicantService.getApplicantsByCurrentUser());
//    }
    @GetMapping("")
    public PaginatedAppResponseDto getAllAppsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir){
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        return this.applicantService.getAllAppsByPage(page, size,sortBy, sortDir);
    }
//    @GetMapping("/{applicantId}")
//    public ResponseEntity<ApplicantResponseDto> getDetail(@PathVariable Long applicantId) {
//        return ResponseEntity.ok(applicantService.getApplicantDetail(applicantId));
//    }


    @GetMapping("/detail/{applicantId}")
    public ResponseEntity<ApplicantResponseDto> getApplicantDetail(@PathVariable Long applicantId) {
        return ResponseEntity.ok(applicantService.getApplicantDetail(applicantId));
    }

    @DeleteMapping("/delete/{applicantId}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable Long applicantId) {
        applicantService.deleteApplicant(applicantId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/resume-link/{filename}")
    public ResponseEntity<Resource> getResumeLink(@PathVariable String filename) {
        Resource resource = applicantService.getResumeResource(filename);
        String contentType = applicantService.getContentType(filename);
        boolean preview = applicantService.isPreviewable(filename);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType));

        if (preview) {
            responseBuilder.header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"");
        } else {
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");
        }

        return responseBuilder.body(resource);
    }




}
