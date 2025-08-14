package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.Activity.ActivityResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SkillDto.CreateSkillRequest;
import com.example.WorkWite_Repo_BE.dtos.SkillDto.SkillResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SkillDto.UpdateSkillRequest;
import com.example.WorkWite_Repo_BE.entities.Activity;
import com.example.WorkWite_Repo_BE.entities.Skill;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.SkillJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkillService {
    private final SkillJpaRepository skillJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;


    public SkillService(SkillJpaRepository skillJpaRepository, ResumeJpaRepository resumeJpaRepository) {
        this.skillJpaRepository = skillJpaRepository;
        this.resumeJpaRepository = resumeJpaRepository;
    }

    // Convert Skill to SkillResponseDto
    public SkillResponseDto convertDto (Skill skill) {
        return new SkillResponseDto(
                skill.getSkillName()
        );
    }
    // Create a new skill
    public SkillResponseDto createSkill(CreateSkillRequest createSkillRequest, Long resumeId) {
        Resume resume = resumeJpaRepository.findById(resumeId).orElse(null);
        if (resume == null) {
            return null; // or throw an exception
        }

        Skill skill = new Skill();
        skill.setSkillName(createSkillRequest.getSkillName());
        skill.setResume(resume);

        Skill savedSkill = skillJpaRepository.save(skill);
        return convertDto(savedSkill);
    }

    // Get skill by id
    public SkillResponseDto getSkillById(Long id) {
        Skill skill = skillJpaRepository.findById(id).orElse(null);
        if (skill == null) return null;
        return convertDto(skill);
    }

    // Update skill
    public SkillResponseDto updateSkill(Long id, UpdateSkillRequest updateRequest) {
        Skill skill = skillJpaRepository.findById(id).orElse(null);
        if (skill == null) return null;
        skill.setSkillName(updateRequest.getSkillName());
        Skill updatedSkill = skillJpaRepository.save(skill);
        return convertDto(updatedSkill);
    }

    // Delete skill
    public boolean deleteSkill(Long id) {
        if (!skillJpaRepository.existsById(id)) return false;
        skillJpaRepository.deleteById(id);
        return true;
    }
}
