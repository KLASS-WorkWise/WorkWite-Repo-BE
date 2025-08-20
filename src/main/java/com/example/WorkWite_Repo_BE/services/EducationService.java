package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.Education.CreatEducationRequestDto;
import com.example.WorkWite_Repo_BE.dtos.Education.EducationResponseDto;
import com.example.WorkWite_Repo_BE.entities.Education;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.EducationJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class EducationService {
    private final EducationJpaRepository educationJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;

    public EducationService(EducationJpaRepository educationJpaRepository, ResumeJpaRepository resumeJpaRepository) {
        this.educationJpaRepository = educationJpaRepository;
        this.resumeJpaRepository = resumeJpaRepository;
    }

    // Convert từ Education entity sang EducationResponseDto
    private EducationResponseDto convertToDto(Education education) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new EducationResponseDto(
                education.getId(),
                education.getSchoolName(),
                education.getDegree(),
                education.getMajor(),
                education.getStartYear(),
                education.getEndYear(),
                education.getGPA());
    }

    // Phương thức tạo Education mới
    public EducationResponseDto createEducation(CreatEducationRequestDto creatEducationRequestDto, Long  resumeId) {
        Education education1 = new Education();
        Resume resume = resumeJpaRepository.findById(resumeId).orElse(null);
        education1.setSchoolName(creatEducationRequestDto.getSchoolName());
        education1.setDegree(creatEducationRequestDto.getDegree());
        education1.setMajor(creatEducationRequestDto.getMajor());
        education1.setStartYear(creatEducationRequestDto.getStartYear());
        education1.setEndYear(creatEducationRequestDto.getEndYear());
        education1.setGPA(creatEducationRequestDto.getGPA());

        education1.setResume(resume);
        Education educationNew = educationJpaRepository.save(education1);

        return convertToDto(educationNew);
    }

    // Lấy tất cả education theo resumeId
    public List<EducationResponseDto> getAllEducationsByResumeId(Long resumeId) {
        List<Education> educations = educationJpaRepository.findByResumeId(resumeId);
        return educations.stream().map(this::convertToDto).toList();
    }

    // Lấy education theo id
    public EducationResponseDto getEducationById(Long id) {
        Education education = educationJpaRepository.findById(Math.toIntExact(id)).orElse(null);
        if (education == null) return null;
        return convertToDto(education);
    }

    // Xóa education theo id
    public boolean deleteEducation(Long id) {
        if (!educationJpaRepository.existsById(Math.toIntExact(id))) return false;
        educationJpaRepository.deleteById(Math.toIntExact(id));
        return true;
    }

}
