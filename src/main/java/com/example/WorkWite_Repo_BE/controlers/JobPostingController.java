package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.services.JobPostingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    @PostMapping
    public ResponseEntity<JobPostingResponseDTO> createJobPosting(@Valid @RequestBody JobPostingRequestDTO requestDTO) {
        JobPostingResponseDTO responseDTO = jobPostingService.createJobPosting(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponseDTO> getJobPosting(@PathVariable Long id) {
        JobPostingResponseDTO responseDTO = jobPostingService.getJobPosting(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<JobPostingResponseDTO>> searchJobPostings(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String salaryRange,
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) String requiredSkills,
        @RequestParam(required = false) String requiredDegree,
        @RequestParam(required = false) Integer minExperience,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
    List<JobPostingResponseDTO> responseDTOs = jobPostingService.searchJobPostings(
        category,
        location,
        salaryRange,
        jobType,
        requiredSkills,
        requiredDegree,
        minExperience,
        page,
        size
    );
    return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingResponseDTO> updateJobPosting(@PathVariable Long id, @Valid @RequestBody JobPostingRequestDTO requestDTO) {
        JobPostingResponseDTO responseDTO = jobPostingService.updateJobPosting(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable Long id) {
        jobPostingService.deleteJobPosting(id);
        return ResponseEntity.noContent().build();
    }
}