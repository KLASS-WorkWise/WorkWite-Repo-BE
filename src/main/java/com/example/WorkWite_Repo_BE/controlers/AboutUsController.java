package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.AboutUsResponseDto;
import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.CreateAboutUsRequestDto;
import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.UpdateAboutUsRequestDto;
import com.example.WorkWite_Repo_BE.services.AboutUsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aboutus")
public class AboutUsController {
    private final AboutUsService aboutUsService;

    public AboutUsController(AboutUsService aboutUsService) {
        this.aboutUsService = aboutUsService;
    }
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDto>> getAllBlogs(){
        List<AboutUsResponseDto> aboutUs = aboutUsService.getAllAboutUs();
        return ResponseEntity.ok(aboutUs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AboutUsResponseDto> getAboutUsById(Long id){
        AboutUsResponseDto aboutUs = aboutUsService.getAboutUsById(id);
        if (aboutUs == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aboutUs);
    }

    @PostMapping
    public ResponseEntity<AboutUsResponseDto> createAboutUs(@RequestBody CreateAboutUsRequestDto requestDto){
        AboutUsResponseDto aboutUs = aboutUsService.createAboutUs(requestDto);
        return ResponseEntity.ok(aboutUs);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAboutUs(@PathVariable Long id) {
        boolean deleted = aboutUsService.deleteAboutUsById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<AboutUsResponseDto> updateAboutUs(
            @PathVariable Long id,
            @RequestBody UpdateAboutUsRequestDto updateDto) {
        AboutUsResponseDto updatedAboutUs = aboutUsService.updateAboutUs(id, updateDto);
        if (updatedAboutUs == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAboutUs);
    }

}
