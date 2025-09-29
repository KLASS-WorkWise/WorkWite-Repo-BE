package com.example.WorkWite_Repo_BE.controlers;


import com.example.WorkWite_Repo_BE.api.RestResponse;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedEmployeeListJobResponseDto;
import com.example.WorkWite_Repo_BE.dtos.savejob.PaginatedSaveJobResponseDto;
import com.example.WorkWite_Repo_BE.dtos.savejob.SavedJobDTO;
import com.example.WorkWite_Repo_BE.services.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobPostingId}")
    public ResponseEntity<SavedJobDTO> saveJob(@PathVariable Long jobPostingId) {
        return ResponseEntity.ok(savedJobService.saveJob(jobPostingId));
    }

//    @GetMapping
//    public ResponseEntity<List<SavedJobDTO>> getMySavedJobs() {
//        return ResponseEntity.ok(savedJobService.getMySavedJobs());
//    }
    @GetMapping("")
    public ResponseEntity<RestResponse<PaginatedSaveJobResponseDto<SavedJobDTO>>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "savedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        RestResponse<PaginatedSaveJobResponseDto<SavedJobDTO>> response = savedJobService.getMySavedJobs(
                page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSavedJob(@PathVariable Long id) {
        savedJobService.removeSavedJob(id);
        return ResponseEntity.noContent().build();
    }
}
