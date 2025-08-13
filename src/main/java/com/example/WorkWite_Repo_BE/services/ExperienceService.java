package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ExperienceDto.ExperienceResponseDto;
import com.example.WorkWite_Repo_BE.entities.Experience;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.ExperienceJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.stereotype.Service;

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
}
