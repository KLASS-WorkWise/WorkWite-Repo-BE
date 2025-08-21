package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ResumeDto.CreatResumeRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.UpdataResumeRequestDto;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.repositories.*;
import jakarta.transaction.Transactional;
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
    private final EducationJpaRepository educationJpaRepository;
    private final AwardJpaRepository awardJpaRepository;
    private final ActivityJpaRepository activityJpaRepository;
    private final ExperienceJpaRepository experienceJpaRepository;



    public ResumeService(ResumeJpaRepository resumeRepository, CandidateJpaRepository candidateJpaRepository, EducationService educationService, ExperienceService experienceService, ActivityService activityService, AwardService awardService, EducationJpaRepository educationJpaRepository, AwardJpaRepository awardJpaRepository, ActivityJpaRepository activityJpaRepository, ExperienceJpaRepository experienceJpaRepository) {
        this.resumeRepository = resumeRepository;
        this.candidateJpaRepository = candidateJpaRepository;
        this.educationService = educationService;
        this.experienceService = experienceService;
        this.activityService = activityService;
        this.awardService = awardService;
        this.educationJpaRepository = educationJpaRepository;
        this.awardJpaRepository = awardJpaRepository;
        this.activityJpaRepository = activityJpaRepository;
        this.experienceJpaRepository = experienceJpaRepository;

    }

    // Tạo mới Resume , activity, award, education,exp
    public ResumeResponseDto creatResume(Long candidateId, CreatResumeRequestDto creatResumeRequestDto) {
        Resume resume1 = new Resume();
        Candidate candidate = candidateJpaRepository.findById(candidateId).orElse(null);
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

        // Xử lý skillsResumes (ElementCollection)
        if (creatResumeRequestDto.getSkillsResumes() != null) {
            resume1.setSkillsResumes(creatResumeRequestDto.getSkillsResumes());
            resumeRepository.save(resume1);
        }

        Resume resumeWithChildren = resumeRepository.findById(resume1.getId()).orElse(null);
        // Truy vấn từng list liên quan
        List educations = educationJpaRepository.findByResumeId(resume1.getId());
        List awards = awardJpaRepository.findByResumeId(resume1.getId());
        List activities = activityJpaRepository.findByResumeId(resume1.getId());
        List experiences = experienceJpaRepository.findByResumeId(resume1.getId());
        List<String> skillsResumes = resumeWithChildren.getSkillsResumes();
        // Gán vào resumeWithChildren
        resumeWithChildren.setEducations(educations);
        resumeWithChildren.setAwards(awards);
        resumeWithChildren.setActivities(activities);
        resumeWithChildren.setExperiences(experiences);
        resumeWithChildren.setSkillsResumes(skillsResumes);
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
        if (resume == null) {
            return null;
        }
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
            // Cập nhật skillsResumes nếu có truyền lên
            if (resumeUpdateDto.getSkillsResumes() != null) {
                resume.setSkillsResumes(resumeUpdateDto.getSkillsResumes());
            }
            // Cập nhật education nếu có truyền lên
            if (resumeUpdateDto.getEducations() != null) {
                // Xóa hết education cũ
                educationJpaRepository.deleteByResumeId(resume.getId());
                // Thêm mới lại danh sách education
                resumeUpdateDto.getEducations().forEach(eduDto -> {
                    com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto newEdu = new com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto();
                    newEdu.setResumeId(resume.getId());
                    newEdu.setSchoolName(eduDto.getSchoolName());
                    newEdu.setDegree(eduDto.getDegree());
                    newEdu.setMajor(eduDto.getMajor());
                    newEdu.setStartYear(eduDto.getStartYear());
                    newEdu.setEndYear(eduDto.getEndYear());
                    educationService.createEducation(newEdu, resume.getId());
                });
            }
            // Cập nhật activities nếu có truyền lên
            if (resumeUpdateDto.getActivities() != null) {
                activityJpaRepository.deleteByResumeId(resume.getId());
                resumeUpdateDto.getActivities().forEach(actDto -> {
                    com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto newAct = new com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto();
                    newAct.setActivityName(actDto.getActivityName());
                    newAct.setRole(actDto.getRole());
                    newAct.setStartYear(actDto.getStartYear());
                    newAct.setEndYear(actDto.getEndYear());
                    activityService.createActivity(newAct, resume.getId());
                });
            }
            // Cập nhật awards nếu có truyền lên
            if (resumeUpdateDto.getAwards() != null) {
                awardJpaRepository.deleteByResumeId(resume.getId());
                resumeUpdateDto.getAwards().forEach(awardDto -> {
                    com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto newAward = new com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto();
                    newAward.setAwardName(awardDto.getAwardName());
                    newAward.setAwardYear(awardDto.getAwardYear());
                    awardService.createAward(newAward, resume.getId());
                });
            }

            // Cập nhật experiences nếu có truyền lên
            if (resumeUpdateDto.getExperiences() != null) {
                experienceJpaRepository.deleteByResumeId(resume.getId());
                resumeUpdateDto.getExperiences().forEach(expDto -> {
                    com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto newExp = new com.example.WorkWite_Repo_BE.dtos.ExperienceDto.CreatExperienceRequestDto();
                    newExp.setCompanyName(expDto.getCompanyName());
                    newExp.setPosition(expDto.getPosition());
                    newExp.setStartYear(expDto.getStartYear());
                    newExp.setEndYear(expDto.getEndYear());
                    newExp.setDescription(expDto.getDescription());
                    experienceService.createExperience(newExp, resume.getId());
                });
            }

            resumeRepository.save(resume);
            // Truy vấn lại các list liên quan sau khi cập nhật
            List educations = educationJpaRepository.findByResumeId(resume.getId());
            List awards = awardJpaRepository.findByResumeId(resume.getId());
            List activities = activityJpaRepository.findByResumeId(resume.getId());
            List experiences = experienceJpaRepository.findByResumeId(resume.getId());
            resume.setEducations(educations);
            resume.setAwards(awards);
            resume.setActivities(activities);
            resume.setExperiences(experiences);
        }
        return convertToDto(resume);
    }

    @Transactional
    public void deleteResumeById(Long id) {
        //fix lỗi xóa k đc resume
        // phải xóa các bản ghi con trước khi xóa resume
        // Xóa các bản ghi con trước khi xóa resume
        awardJpaRepository.deleteByResumeId(id);
        educationJpaRepository.deleteByResumeId(id);
        activityJpaRepository.deleteByResumeId(id);
        experienceJpaRepository.deleteByResumeId(id);
        resumeRepository.deleteById(id);
    }

    private ResumeResponseDto convertToDto(Resume resume) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = "";
        if (resume.getCreatedAt() != null) {
            createdAtStr = resume.getCreatedAt().format(formatter);
        } else {
            createdAtStr = null;
        }
        // Fix: tra ve list rỗng nếu không có dữ liệu
        return new ResumeResponseDto(
                resume.getId(),
                resume.getProfilePicture(),
                resume.getFullName(),
                resume.getEmail(),
                resume.getPhone(),
                createdAtStr,
                resume.getJobTitle(),
                resume.getActivities() == null ? java.util.Collections.emptyList() : resume.getActivities(),
                resume.getEducations() == null ? java.util.Collections.emptyList() : resume.getEducations(),
                resume.getAwards() == null ? java.util.Collections.emptyList() : resume.getAwards(),
                resume.getApplicants() == null ? java.util.Collections.emptyList() : resume.getApplicants(),
                resume.getSkillsResumes() == null ? java.util.Collections.emptyList() : resume.getSkillsResumes(),
                resume.getSummary()
        );
    }
}
