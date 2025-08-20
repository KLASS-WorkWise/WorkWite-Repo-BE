package com.example.WorkWite_Repo_BE.controlers;


import com.example.WorkWite_Repo_BE.dtos.savejob.SavedJobDTO;
import com.example.WorkWite_Repo_BE.services.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobPostingId}")
    public ResponseEntity<SavedJobDTO> saveJob(@PathVariable Long jobPostingId) {
        return ResponseEntity.ok(savedJobService.saveJob(jobPostingId));
    }

    @GetMapping
    public ResponseEntity<List<SavedJobDTO>> getMySavedJobs() {
        return ResponseEntity.ok(savedJobService.getMySavedJobs());
    }

    @DeleteMapping("/{jobPostingId}")
    public ResponseEntity<Void> removeSavedJob(@PathVariable Long jobPostingId) {
        savedJobService.removeSavedJob(jobPostingId);
        return ResponseEntity.noContent().build();
    }
}
