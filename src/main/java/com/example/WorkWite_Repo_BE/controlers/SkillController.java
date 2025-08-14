package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.SkillDto.CreateSkillRequest;
import com.example.WorkWite_Repo_BE.dtos.SkillDto.SkillResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SkillDto.UpdateSkillRequest;
import com.example.WorkWite_Repo_BE.services.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<SkillResponseDto> createSkill(@PathVariable Long resumeId, @RequestBody CreateSkillRequest request) {
        SkillResponseDto skill = skillService.createSkill(request, resumeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDto> getSkillById(@PathVariable Long id) {
        SkillResponseDto skill = skillService.getSkillById(id);
        if (skill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skill);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SkillResponseDto> updateSkill(@PathVariable Long id, @RequestBody UpdateSkillRequest request) {
        SkillResponseDto updated = skillService.updateSkill(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        boolean deleted = skillService.deleteSkill(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
