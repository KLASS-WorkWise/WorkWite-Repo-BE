package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingUpdateDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingPaginatedDTO;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        // Lấy username từ token đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String username;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            username = authentication.getName();
        } else {
            throw new RuntimeException("Unauthorized: Cannot get username from token");
        }
        // Tìm employer theo user đăng nhập
        Employers employer = employerRepository.findByUserId(
            getUserIdByUsername(username)
        ).orElseThrow(() -> new RuntimeException("Employer not found for user: " + username));

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

    // Helper: lấy userId từ username
    private Long getUserIdByUsername(String username) {
        // Nếu có UserRepository thì dùng, nếu không thì lấy từ employerRepository
        Optional<Employers> employerOpt = employerRepository.findAll().stream()
            .filter(e -> e.getUser() != null && username.equals(e.getUser().getUsername()))
            .findFirst();
        if (employerOpt.isPresent()) {
            return employerOpt.get().getUser().getId();
        }
        throw new RuntimeException("User not found for username: " + username);
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
    public JobPostingResponseDTO updateJobPosting(Long id, JobPostingUpdateDTO updateDTO) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));

        // Lấy username từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            username = authentication.getName();
        }
        if (username == null) {
            throw new RuntimeException("Unauthorized: Cannot get username from token");
        }
        // Chỉ kiểm tra username của user tạo job với user đăng nhập
        if (!jobPosting.getEmployer().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden: Only the owner employer can update this job posting");
        }

        if (updateDTO.getEmployerId() != null) {
            Employers employer = employerRepository.findById(updateDTO.getEmployerId())
                    .orElseThrow(() -> new RuntimeException("Employer not found with id: " + updateDTO.getEmployerId()));
            jobPosting.setEmployer(employer);
        }
        if (updateDTO.getTitle() != null) jobPosting.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null) jobPosting.setDescription(updateDTO.getDescription());
        if (updateDTO.getLocation() != null) jobPosting.setLocation(updateDTO.getLocation());
        if (updateDTO.getSalaryRange() != null) jobPosting.setSalaryRange(updateDTO.getSalaryRange());
        if (updateDTO.getJobType() != null) jobPosting.setJobType(updateDTO.getJobType());
        if (updateDTO.getCategory() != null) jobPosting.setCategory(updateDTO.getCategory());
        if (updateDTO.getRequiredSkills() != null) jobPosting.setRequiredSkills(updateDTO.getRequiredSkills());
        if (updateDTO.getMinExperience() != null) jobPosting.setMinExperience(updateDTO.getMinExperience());
        if (updateDTO.getRequiredDegree() != null) jobPosting.setRequiredDegree(updateDTO.getRequiredDegree());
        if (updateDTO.getEndAt() != null) jobPosting.setEndAt(updateDTO.getEndAt());
        if (updateDTO.getStatus() != null) jobPosting.setStatus(updateDTO.getStatus());

        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return mapToResponseDTO(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));

        // Lấy username từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            username = authentication.getName();
        }
        if (username == null) {
            throw new RuntimeException("Unauthorized: Cannot get username from token");
        }
        // Chỉ kiểm tra username của user tạo job với user đăng nhập
        if (!jobPosting.getEmployer().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden: Only the owner employer can delete this job posting");
        }

        jobPostingRepository.deleteById(id);
    }

    private JobPostingResponseDTO mapToResponseDTO(JobPosting jobPosting) {
        JobPostingResponseDTO responseDTO = new JobPostingResponseDTO();
        responseDTO.setId(jobPosting.getId());
        responseDTO.setEmployerId(jobPosting.getEmployer().getId());
        // Lấy tên employer từ user hoặc companyInformation nếu có
        String employerName = null;
        if (jobPosting.getEmployer().getUser() != null) {
            employerName = jobPosting.getEmployer().getUser().getFullName();
        } else if (jobPosting.getEmployer().getCompanyInformation() != null) {
            employerName = jobPosting.getEmployer().getCompanyInformation().getCompanyName();
        }
        responseDTO.setEmployerName(employerName);
        responseDTO.setTitle(jobPosting.getTitle());
        responseDTO.setDescription(jobPosting.getDescription());
        responseDTO.setLocation(jobPosting.getLocation());
        responseDTO.setSalaryRange(jobPosting.getSalaryRange());
        responseDTO.setJobType(jobPosting.getJobType());
        responseDTO.setCategory(jobPosting.getCategory());
        responseDTO.setRequiredSkills(jobPosting.getRequiredSkills());
        responseDTO.setMinExperience(jobPosting.getMinExperience());
        responseDTO.setRequiredDegree(jobPosting.getRequiredDegree());
        responseDTO.setCreatedAt(jobPosting.getCreatedAt());
        responseDTO.setEndAt(jobPosting.getEndAt());
        responseDTO.setStatus(jobPosting.getStatus());
        return responseDTO;
    }
    @Override
    public JobPostingPaginatedDTO searchJobPostings(
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
        org.springframework.data.domain.Page<JobPosting> jobPostingsPage = jobPostingRepository.findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredDegreeContaining(
            category != null ? category : "",
            location != null ? location : "",
            salaryRange != null ? salaryRange : "",
            jobType != null ? jobType : "",
            requiredDegree != null ? requiredDegree : "",
            pageable
        );
        // Nếu filter requiredSkills, lọc tiếp trên kết quả trả về
        List<JobPosting> filtered = jobPostingsPage.getContent();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            filtered = filtered.stream()
                .filter(jp -> jp.getRequiredSkills() != null && jp.getRequiredSkills().stream().anyMatch(skill -> skill.toLowerCase().contains(requiredSkills.toLowerCase())))
                .collect(Collectors.toList());
        }
        if (minExperience != null) {
            filtered = filtered.stream()
                .filter(jp -> jp.getMinExperience() != null && jp.getMinExperience() >= minExperience)
                .collect(Collectors.toList());
        }
        JobPostingPaginatedDTO paginatedDTO = new JobPostingPaginatedDTO();
        paginatedDTO.setJobs(filtered.stream().map(this::mapToResponseDTO).collect(Collectors.toList()));
        paginatedDTO.setPage(page);
        paginatedDTO.setSize(size);
        paginatedDTO.setTotalElements(jobPostingsPage.getTotalElements());
        paginatedDTO.setTotalPages(jobPostingsPage.getTotalPages());
        return paginatedDTO;
    }
}