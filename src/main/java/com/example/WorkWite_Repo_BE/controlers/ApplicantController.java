package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.services.ApplicantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;

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


}
