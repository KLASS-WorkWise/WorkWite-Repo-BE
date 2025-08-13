package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostingResponseDTO;
import java.util.List;

public interface JobPostingService {
    JobPostingResponseDTO createJobPosting(JobPostingRequestDTO requestDTO);
    JobPostingResponseDTO getJobPosting(Long id);
    List<JobPostingResponseDTO> getAllJobPostings();
    JobPostingResponseDTO updateJobPosting(Long id, JobPostingRequestDTO requestDTO);
    void deleteJobPosting(Long id);

    // Hàm tìm kiếm, lọc, phân trang
    List<JobPostingResponseDTO> searchJobPostings(
        String category,
        String location,
        String salaryRange,
        String jobType,
        String requiredSkills,
        String requiredDegree,
        Integer minExperience,
        Integer page,
        Integer size
    );
}