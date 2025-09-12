package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.ExperienceResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.UpdateExperienceRequestDto;
import com.example.WorkWite_Repo_BE.services.ExperienceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {
    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<ExperienceResponseDto> createExperience(@PathVariable Long resumeId, @RequestBody CreatExperienceRequestDto requestDto) {
        ExperienceResponseDto experience = experienceService.createExperience(requestDto, resumeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(experience);
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<ExperienceResponseDto>> getAllExperiencesByResumeId(@PathVariable Long resumeId) {
        List<ExperienceResponseDto> experiences = experienceService.getAllExperiencesByResumeId(resumeId);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceResponseDto> getExperienceById(@PathVariable Long id) {
        ExperienceResponseDto experience = experienceService.getExperienceById(id);
        if (experience == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(experience);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExperienceResponseDto> updateExperience(@PathVariable Long id, @RequestBody UpdateExperienceRequestDto requestDto) {
        ExperienceResponseDto updated = experienceService.updateExperience(id, requestDto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        boolean deleted = experienceService.deleteExperience(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

