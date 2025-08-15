package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.services.ApplicantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/applicant")
@Validated
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;


    @PostMapping("/{jobId}/apply")
    public ResponseEntity<ApplicantResponseDto> applyJob(
            @PathVariable  Long jobId,
            @RequestBody @Valid ApplicantRequestDto applicantRequestDto) throws Exception {

        ApplicantResponseDto response = applicantService.applyJob(jobId, applicantRequestDto);
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




}
