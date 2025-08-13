package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ResumeDto.CreatResumeRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.UpdataResumeRequestDto;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeJpaRepository resumeRepository;
    private final CandidateJpaRepository candidateJpaRepository;
    private final EducationService educationService;
    private final ExperienceService experienceService;
    private final ActivityService activityService;
    private final AwardService awardService;


    public ResumeService(ResumeJpaRepository resumeRepository, CandidateJpaRepository candidateJpaRepository, EducationService educationService, ExperienceService experienceService, ActivityService activityService, AwardService awardService) {
        this.resumeRepository = resumeRepository;
        this.candidateJpaRepository = candidateJpaRepository;
        this.educationService = educationService;
        this.experienceService = experienceService;
        this.activityService = activityService;
        this.awardService = awardService;
    }

    private ResumeResponseDto convertToDto(Resume resume) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = "";
        if (resume.getCreatedAt() != null) {
            createdAtStr = resume.getCreatedAt().format(formatter);
        } else {
            createdAtStr = null;
        }
        return new ResumeResponseDto(
                resume.getId(),
                resume.getProfilePicture(),
                resume.getFullName(),
                resume.getEmail(),
                resume.getPhone(),
                createdAtStr,
                resume.getJobTitle(),
                resume.getActivities(),
                resume.getEducations(),
                resume.getAwards(),
                resume.getApplications(),
                resume.getSummary()
        );
    }

    // Tạo mới Resume , activity, award, education,exp
    public ResumeResponseDto creatResume(CreatResumeRequestDto creatResumeRequestDto) {
        Resume resume1 = new Resume();
        Candidate candidate = candidateJpaRepository.findById(creatResumeRequestDto.getCandidateId()).orElse(null);
        resume1.setCandidate(candidate);
        resume1.setFullName(creatResumeRequestDto.getFullName());
        resume1.setEmail(creatResumeRequestDto.getEmail());
        resume1.setPhone(creatResumeRequestDto.getPhone());
        resume1.setProfilePicture(creatResumeRequestDto.getProfilePicture());
        resume1.setSummary(creatResumeRequestDto.getSummary());
        resume1.setJobTitle(creatResumeRequestDto.getJobTitle());
        resume1.setCreatedAt(LocalDateTime.now());
        resumeRepository.save(resume1);

        if (creatResumeRequestDto.getEducations() != null) {
            creatResumeRequestDto.getEducations().forEach(education -> {
                educationService.createEducation(education, resume1.getId());
            });
        }
        if (creatResumeRequestDto.getAwards() != null) {
            creatResumeRequestDto.getAwards().forEach(award -> {
                awardService.createAward(award, resume1.getId());
            });
        }
        if (creatResumeRequestDto.getActivities() != null) {
            creatResumeRequestDto.getActivities().forEach(activity -> {
                activityService.createActivity(activity, resume1.getId());
            });
        }
        if (creatResumeRequestDto.getExperiences() != null) {
            creatResumeRequestDto.getExperiences().forEach(experience -> {
                experienceService.createExperience(experience, resume1.getId());
            });
        }

        Resume resumeWithChildren = resumeRepository.findById(resume1.getId()).orElse(null);
        return convertToDto(resumeWithChildren);
    }

    // Lấy tất cả Resume
    public List<ResumeResponseDto> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return resumes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Lấy Resume theo ID
    public ResumeResponseDto getResumeById(Long id) {
        Resume resume = resumeRepository.findById(id).orElse(null);
        return convertToDto(resume);
    }

    // Cập nhật Resume
    public ResumeResponseDto updateResume(Long id, UpdataResumeRequestDto resumeUpdateDto) {
        Resume resume = resumeRepository.findById(id).orElse(null);
        if (resume != null) {
            // Cập nhật các trường của Resume
            resume.setFullName(resumeUpdateDto.getFullName());
            resume.setEmail(resumeUpdateDto.getEmail());
            resume.setPhone(resumeUpdateDto.getPhone());
            resume.setProfilePicture(resumeUpdateDto.getProfilePicture());
            resume.setSummary(resumeUpdateDto.getSummary());
            resume.setJobTitle(resumeUpdateDto.getJobTitle());

            // Cập nhật các liên kết với Education, Award, Activity
            // (Giả sử các ID liên kết này được truyền từ DTO)


            resumeRepository.save(resume);
        }
        return convertToDto(resume);
    }

    public void deleteResumeById(Long id) {
        resumeRepository.deleteById(id);
    }
}
