package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto;
import com.example.WorkWite_Repo_BE.dtos.Education.EducationResponseDto;
import com.example.WorkWite_Repo_BE.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/educations")
public class EducationController {
    private final EducationService educationService;

    @Autowired
    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<EducationResponseDto> createEducation(@PathVariable Long resumeId, @RequestBody CreatEducationRequestDto creatEducationRequestDto) {
        EducationResponseDto educationResponseDto = educationService.createEducation(creatEducationRequestDto, resumeId);
        return new ResponseEntity<>(educationResponseDto, HttpStatus.OK);
    }
}
