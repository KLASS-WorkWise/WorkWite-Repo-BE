package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingUpdateDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingPaginatedDTO;
import java.util.List;

public interface JobPostingService {
    JobPostingResponseDTO createJobPosting(JobPostingRequestDTO requestDTO);
    JobPostingResponseDTO getJobPosting(Long id);
    List<JobPostingResponseDTO> getAllJobPostings();
    JobPostingResponseDTO updateJobPosting(Long id, JobPostingUpdateDTO updateDTO);
    void deleteJobPosting(Long id);

    // Search/filter/paginate
    JobPostingPaginatedDTO searchJobPostings(
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