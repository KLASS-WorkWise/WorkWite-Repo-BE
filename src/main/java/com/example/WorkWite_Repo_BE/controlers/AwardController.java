package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.AwardDto.AwardResponseDto;
import com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto;
import com.example.WorkWite_Repo_BE.services.AwardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/awards")
public class AwardController {
    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<AwardResponseDto> createAward(@PathVariable Long resumeId, @RequestBody CreatAwardRequestDto requestDto) {
        AwardResponseDto award = awardService.createAward(requestDto, resumeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(award);
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<AwardResponseDto>> getAllAwardsByResumeId(@PathVariable Long resumeId) {
        List<AwardResponseDto> awards = awardService.getAllAwardsByResumeId(resumeId);
        return ResponseEntity.ok(awards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AwardResponseDto> getAwardById(@PathVariable Long id) {
        AwardResponseDto award = awardService.getAwardById(id);
        if (award == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(award);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAward(@PathVariable Long id) {
        boolean deleted = awardService.deleteAward(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

