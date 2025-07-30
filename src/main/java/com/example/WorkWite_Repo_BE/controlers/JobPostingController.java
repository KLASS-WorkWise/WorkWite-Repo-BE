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
    public ResponseEntity<List<JobPostingResponseDTO>> getAllJobPostings() {
        List<JobPostingResponseDTO> responseDTOs = jobPostingService.getAllJobPostings();
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