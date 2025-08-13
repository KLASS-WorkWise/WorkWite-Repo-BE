package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.AwardDto.AwardResponseDto;
import com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto;
import com.example.WorkWite_Repo_BE.services.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/awards")
public class AwardController {
    private final AwardService awardService;

    @Autowired
    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<AwardResponseDto> createAward(@PathVariable Long resumeId, @RequestBody CreatAwardRequestDto creatAwardRequestDto) {
        AwardResponseDto awardResponseDto = awardService.createAward(creatAwardRequestDto, resumeId);
        return new ResponseEntity<>(awardResponseDto, HttpStatus.OK);
    }
}
