package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.ExperienceResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.UpdateExperienceRequestDto;
import com.example.WorkWite_Repo_BE.entities.Experience;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.ExperienceJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceService {
    private final ExperienceJpaRepository experienceJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;

    public ExperienceService(ExperienceJpaRepository experienceJpaRepository, ResumeJpaRepository resumeJpaRepository) {
        this.experienceJpaRepository = experienceJpaRepository;
        this.resumeJpaRepository = resumeJpaRepository;
    }

    // convert entity thanh responedto
    public ExperienceResponseDto convertToDto(Experience experience) {
        return new ExperienceResponseDto(
                experience.getId(),
                experience.getCompanyName(),
                experience.getPosition(),
                experience.getStartYear(),
                experience.getEndYear(),
                experience.getDescription()
        );
    }

    public ExperienceResponseDto createExperience(CreatExperienceRequestDto creatExperienceRequestDto, Long resumeId) {
        Resume resume = resumeJpaRepository.findById(resumeId).orElse(null);
        Experience experience = new Experience();
        experience.setCompanyName(creatExperienceRequestDto.getCompanyName());
        experience.setPosition(creatExperienceRequestDto.getPosition());
        experience.setStartYear(creatExperienceRequestDto.getStartYear());
        experience.setEndYear(creatExperienceRequestDto.getEndYear());
        experience.setDescription(creatExperienceRequestDto.getDescription());
        experience.setResume(resume);
        Experience addNew = experienceJpaRepository.save(experience);
        return convertToDto(addNew);
    }

    // Lấy tất cả experience theo resumeId
    public List<ExperienceResponseDto> getAllExperiencesByResumeId(Long resumeId) {
        List<Experience> experiences = experienceJpaRepository.findByResumeId(resumeId);
        return experiences.stream().map(this::convertToDto).toList();
    }

    // Lấy experience theo id
    public ExperienceResponseDto getExperienceById(Long id) {
        Experience experience = experienceJpaRepository.findById(id).orElse(null);
        if (experience == null) return null;
        return convertToDto(experience);
    }

    // Cập nhật experience
    public ExperienceResponseDto updateExperience(Long id, UpdateExperienceRequestDto updateDto) {
        Experience experience = experienceJpaRepository.findById(id).orElse(null);
        if (experience == null) return null;
        experience.setCompanyName(updateDto.getCompanyName());
        experience.setPosition(updateDto.getPosition());
        experience.setStartYear(updateDto.getStartYear());
        experience.setEndYear(updateDto.getEndYear());
        experience.setDescription(updateDto.getDescription());
        Experience updated = experienceJpaRepository.save(experience);
        return convertToDto(updated);
    }

    // Xóa experience theo id
    public boolean deleteExperience(Long id) {
        if (!experienceJpaRepository.existsById(id)) return false;
        experienceJpaRepository.deleteById(id);
        return true;
    }
}
