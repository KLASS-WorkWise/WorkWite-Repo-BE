package com.example.movie.controllers;

import com.example.movie.dtos.savejob.SavedJobDTO;
import com.example.movie.entities.SavedJob;
import com.example.movie.services.SavedJobService;
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
