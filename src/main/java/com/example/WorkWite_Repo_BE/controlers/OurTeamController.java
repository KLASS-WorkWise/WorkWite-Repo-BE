package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.CreateOurTeamRequestDto;
import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.OurTeamResponseDto;
import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.UpdateOurTeamRequestDto;
import com.example.WorkWite_Repo_BE.services.OurTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ourteam")
public class OurTeamController {
    private final OurTeamService ourTeamService;

    public OurTeamController(OurTeamService ourTeamService) {
        this.ourTeamService = ourTeamService;
    }

    @PostMapping
    public ResponseEntity<OurTeamResponseDto> createOurTeam(@RequestBody CreateOurTeamRequestDto dto) {
        OurTeamResponseDto response = ourTeamService.creatOurTeam(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OurTeamResponseDto>> getAllOurTeam() {
        return ResponseEntity.ok(ourTeamService.getAllOurTeam());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OurTeamResponseDto> getOurTeamById(@PathVariable Long id) {
        OurTeamResponseDto response = ourTeamService.getOurTeamById(id);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OurTeamResponseDto> updateOurTeam(@PathVariable Long id, @RequestBody UpdateOurTeamRequestDto dto) {
        OurTeamResponseDto response = ourTeamService.updateOurTeam(id, dto);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOurTeam(@PathVariable Long id) {
        boolean deleted = ourTeamService.deleteOurTeamById(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}