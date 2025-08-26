package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.ResumeDto.CreatResumeRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.UpdataResumeRequestDto;
import com.example.WorkWite_Repo_BE.exceptions.IdInvalidException;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import com.example.WorkWite_Repo_BE.services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;
    private final CandidateJpaRepository candidateJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public ResumeController(ResumeService resumeService, CandidateJpaRepository candidateJpaRepository, UserJpaRepository userJpaRepository) {
        this.resumeService = resumeService;
        this.candidateJpaRepository = candidateJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    //code truyền thông qua candidateId client
//    @PostMapping("/candidate/{candidateId}")
//    public ResponseEntity<ResumeResponseDto> createResume(@PathVariable Long candidateId, @RequestBody CreatResumeRequestDto requestDto) {
//        ResumeResponseDto created = resumeService.creatResume(candidateId, requestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }

    @PostMapping("/me")
    public ResponseEntity<ResumeResponseDto> createResumeForCurrentUser(@RequestBody CreatResumeRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.example.WorkWite_Repo_BE.entities.User user = userJpaRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Candidate candidate = candidateJpaRepository.findByUserId(user.getId());
        ResumeResponseDto created = resumeService.creatResume(candidate.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @PatchMapping("/me/{id}")
    public ResponseEntity<ResumeResponseDto> updateResumeForCurrentUser(@PathVariable Long id, @RequestBody UpdataResumeRequestDto updateDto) throws IdInvalidException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.example.WorkWite_Repo_BE.entities.User user = userJpaRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Candidate candidate = candidateJpaRepository.findByUserId(user.getId());
        ResumeResponseDto resume = resumeService.getResumeById(id);
        if (resume == null || !resume.getId().equals(id)) {
            throw new IdInvalidException("Resume not found with ID:" + id);
        }
        ResumeResponseDto updated = resumeService.updateResume(id, updateDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

//    @GetMapping
//    public ResponseEntity<List<ResumeResponseDto>> getAllResumes() {
//        List<ResumeResponseDto> resumes = resumeService.getAllResumes();
//        return ResponseEntity.ok(resumes);
//    }

    @GetMapping
    public ResponseEntity<List<ResumeResponseDto>> getMyResumes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.example.WorkWite_Repo_BE.entities.User user = userJpaRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Candidate candidate = candidateJpaRepository.findByUserId(user.getId());
        List<ResumeResponseDto> resumes = resumeService.getResumesByCandidateId(candidate.getId());
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

//    @PatchMapping("/{id}")
//    public ResponseEntity<ResumeResponseDto> updateResume(@PathVariable Long id, @RequestBody UpdataResumeRequestDto updateDto) throws IdInvalidException {
//        ResumeResponseDto updated = resumeService.updateResume(id, updateDto);
//        if (updated == null) {
//            throw new IdInvalidException("Resume not found with ID:" +id);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(updated);
//    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResumeById(id);
        return ResponseEntity.noContent().build();
    }
}
