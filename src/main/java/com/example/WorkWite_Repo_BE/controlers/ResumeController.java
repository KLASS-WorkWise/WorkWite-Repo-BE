package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.ResumeDto.CreatResumeRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.UpdataResumeRequestDto;
import com.example.WorkWite_Repo_BE.exceptions.IdInvalidException;
import com.example.WorkWite_Repo_BE.services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/candidate/{candidateId}")
    public ResponseEntity<ResumeResponseDto> createResume(@PathVariable Long candidateId, @RequestBody CreatResumeRequestDto requestDto) {
        ResumeResponseDto created = resumeService.creatResume(candidateId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponseDto>> getAllResumes() {
        List<ResumeResponseDto> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponseDto> getResumeById(@PathVariable Long id) throws IdInvalidException {
        ResumeResponseDto resume = resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found with ID:" +id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resume);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResumeResponseDto> updateResume(@PathVariable Long id, @RequestBody UpdataResumeRequestDto updateDto) throws IdInvalidException {
        ResumeResponseDto updated = resumeService.updateResume(id, updateDto);
        if (updated == null) {
            throw new IdInvalidException("Resume not found with ID:" +id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResumeById(id);
        return ResponseEntity.noContent().build();
    }
}
