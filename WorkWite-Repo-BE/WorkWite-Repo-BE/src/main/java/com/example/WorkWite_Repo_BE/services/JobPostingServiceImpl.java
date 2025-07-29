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
        jobPosting.setDeadline(requestDTO.getDeadline());
        jobPosting.setStatus(requestDTO.getStatus());
        jobPosting.setCreatedAt(LocalDateTime.now());

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
        jobPosting.setDeadline(requestDTO.getDeadline());
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
        responseDTO.setDeadline(jobPosting.getDeadline());
        responseDTO.setStatus(jobPosting.getStatus());
        responseDTO.setCreatedAt(jobPosting.getCreatedAt());
        return responseDTO;
    }
}