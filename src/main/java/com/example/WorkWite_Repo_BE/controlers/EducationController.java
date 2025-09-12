package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto;
import com.example.WorkWite_Repo_BE.dtos.Education.EducationResponseDto;
import com.example.WorkWite_Repo_BE.dtos.Education.UpdateEducationRequestDto;
import com.example.WorkWite_Repo_BE.services.EducationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
public class EducationController {
    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<EducationResponseDto> createEducation(@PathVariable Long resumeId, @RequestBody CreatEducationRequestDto requestDto) {
        EducationResponseDto education = educationService.createEducation(requestDto, resumeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(education);
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<EducationResponseDto>> getAllEducationsByResumeId(@PathVariable Long resumeId) {
        List<EducationResponseDto> educations = educationService.getAllEducationsByResumeId(resumeId);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationResponseDto> getEducationById(@PathVariable Long id) {
        EducationResponseDto education = educationService.getEducationById(id);
        if (education == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(education);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        boolean deleted = educationService.deleteEducation(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

