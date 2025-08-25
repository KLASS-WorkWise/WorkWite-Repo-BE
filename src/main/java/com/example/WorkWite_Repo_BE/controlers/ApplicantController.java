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
@RequestMapping("/applicant")
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
        try {
            Path filePath = Paths.get(ApplicantService.RESUME_UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

            if(!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File không tồn tại");
            }

            // Lấy file extension
            String ext = "";
            int i = filename.lastIndexOf('.');
            if (i > 0) ext = filename.substring(i+1).toLowerCase();

            // Xác định content type
            String contentType;
            boolean preview = false;
            switch(ext){
                case "pdf":
                    contentType = "application/pdf";
                    preview = true; // PDF có thể preview
                    break;
                case "doc":
                    contentType = "application/msword";
                    break;
                case "docx":
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    break;
                default:
                    contentType = "application/octet-stream";
            }

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType));

            if(preview){
                // Cho phép preview trên trình duyệt: inline
                responseBuilder.header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"");
            } else {
                // Buộc download
                responseBuilder.header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");
            }

            return responseBuilder.body(resource);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải file");
        }
    }


}
