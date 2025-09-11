package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ListApplicantResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.services.ApplicantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/applicant")
@Validated
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;


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



    }
