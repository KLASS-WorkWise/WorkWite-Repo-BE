package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final EmployersJpaRepository employerRepository;

    public JobPostingServiceImpl(JobPostingRepository jobPostingRepository, EmployersJpaRepository employerRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    public JobPostingResponseDTO createJobPosting(JobPostingRequestDTO requestDTO) {
        Employers employer = employerRepository.findById(requestDTO.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + requestDTO.getEmployerId()));

        JobPosting jobPosting = new JobPosting();
        jobPosting.setEmployer(employer);
    jobPosting.setTitle(requestDTO.getTitle());
    jobPosting.setDescription(requestDTO.getDescription());
    jobPosting.setLocation(requestDTO.getLocation());
    jobPosting.setSalaryRange(requestDTO.getSalaryRange());
    jobPosting.setJobType(requestDTO.getJobType());
    jobPosting.setCategory(requestDTO.getCategory());
    jobPosting.setRequiredSkills(requestDTO.getRequiredSkills());
    jobPosting.setMinExperience(requestDTO.getMinExperience());
    jobPosting.setRequiredDegree(requestDTO.getRequiredDegree());
    jobPosting.setEndAt(requestDTO.getEndAt());
    jobPosting.setStatus(requestDTO.getStatus());
    jobPosting.setCreatedAt(requestDTO.getCreatedAt() != null ? requestDTO.getCreatedAt() : LocalDateTime.now());

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return mapToResponseDTO(savedJobPosting);
    }

    @Override
    public JobPostingResponseDTO getJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));
        return mapToResponseDTO(jobPosting);
    }

    @Override
    public List<JobPostingResponseDTO> getAllJobPostings() {
        return jobPostingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JobPostingResponseDTO updateJobPosting(Long id, JobPostingRequestDTO requestDTO) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));

        Employers employer = employerRepository.findById(requestDTO.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + requestDTO.getEmployerId()));

        jobPosting.setEmployer(employer);
    jobPosting.setTitle(requestDTO.getTitle());
    jobPosting.setDescription(requestDTO.getDescription());
    jobPosting.setLocation(requestDTO.getLocation());
    jobPosting.setSalaryRange(requestDTO.getSalaryRange());
    jobPosting.setJobType(requestDTO.getJobType());
    jobPosting.setCategory(requestDTO.getCategory());
    jobPosting.setRequiredSkills(requestDTO.getRequiredSkills());
    jobPosting.setMinExperience(requestDTO.getMinExperience());
    jobPosting.setRequiredDegree(requestDTO.getRequiredDegree());
    jobPosting.setEndAt(requestDTO.getEndAt());
    jobPosting.setStatus(requestDTO.getStatus());

        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return mapToResponseDTO(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(Long id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new RuntimeException("Job posting not found with id: " + id);
        }
        jobPostingRepository.deleteById(id);
    }

    private JobPostingResponseDTO mapToResponseDTO(JobPosting jobPosting) {
        JobPostingResponseDTO responseDTO = new JobPostingResponseDTO();
        responseDTO.setId(jobPosting.getId());
        responseDTO.setEmployerId(jobPosting.getEmployer().getId());
        responseDTO.setTitle(jobPosting.getTitle());
        responseDTO.setDescription(jobPosting.getDescription());
        responseDTO.setLocation(jobPosting.getLocation());
        responseDTO.setSalaryRange(jobPosting.getSalaryRange());
        responseDTO.setJobType(jobPosting.getJobType());
        responseDTO.setCategory(jobPosting.getCategory());
        responseDTO.setRequiredSkills(jobPosting.getRequiredSkills());
        responseDTO.setMinExperience(jobPosting.getMinExperience());
        responseDTO.setRequiredDegree(jobPosting.getRequiredDegree());
        responseDTO.setEndAt(jobPosting.getEndAt());
        responseDTO.setStatus(jobPosting.getStatus());
        responseDTO.setCreatedAt(jobPosting.getCreatedAt());
        return responseDTO;
    }
    @Override
    public List<JobPostingResponseDTO> searchJobPostings(
        String category,
        String location,
        String salaryRange,
        String jobType,
        String requiredSkills,
        String requiredDegree,
        Integer minExperience,
        Integer page,
        Integer size
    ) {
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
    org.springframework.data.domain.Page<JobPosting> jobPostingsPage = jobPostingRepository.findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredSkillsContainingAndRequiredDegreeContaining(
        category != null ? category : "",
        location != null ? location : "",
        salaryRange != null ? salaryRange : "",
        jobType != null ? jobType : "",
        requiredSkills != null ? requiredSkills : "",
        requiredDegree != null ? requiredDegree : "",
        pageable
    );
    // Nếu filter minExperience, lọc tiếp trên kết quả
    List<JobPosting> filtered = jobPostingsPage.getContent();
    if (minExperience != null) {
        filtered = filtered.stream()
            .filter(jp -> jp.getMinExperience() != null && jp.getMinExperience() >= minExperience)
            .collect(Collectors.toList());
    }
    return filtered.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
}