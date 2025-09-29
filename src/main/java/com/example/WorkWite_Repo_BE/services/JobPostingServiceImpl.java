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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final EmployersJpaRepository employerRepository;
    private final SystemLogService systemLogService;

    @Autowired
    private UserBalanceService userBalanceService;

    public JobPostingServiceImpl(
            JobPostingRepository jobPostingRepository,
            EmployersJpaRepository employerRepository,
            SystemLogService systemLogService
    ) {
        this.jobPostingRepository = jobPostingRepository;
        this.employerRepository = employerRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public JobPostingResponseDTO createJobPosting(JobPostingRequestDTO requestDTO) {
        // Lấy email hoặc username từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            try {
                java.lang.reflect.Method getEmailMethod = principal.getClass().getMethod("getEmail");
                Object emailObj = getEmailMethod.invoke(principal);
                if (emailObj != null) {
                    actor = emailObj.toString();
                }
            } catch (Exception e) {
                if (principal instanceof UserDetails) {
                    actor = ((UserDetails) principal).getUsername();
                } else {
                    actor = authentication.getName();
                }
            }
        }
        if (actor == null) {
            throw new RuntimeException("Unauthorized: Cannot get actor from token");
        }
        final String finalActor = actor;
        // Tìm employer theo user đăng nhập
        Employers employer = employerRepository.findByUserId(getUserIdByUsername(finalActor))
                .orElseThrow(() -> new RuntimeException("Employer not found for user: " + finalActor));

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
        jobPosting.setCreatedAt(
                requestDTO.getCreatedAt() != null ? requestDTO.getCreatedAt() : LocalDateTime.now()
        );

        // Tính giá bài đăng động
        String postType = requestDTO.getPostType();
        double pricePerDayUSD;
        if (postType != null && postType.equalsIgnoreCase("VIP")) {
            pricePerDayUSD = 2.0; // 5$/ngày cho VIP
        } else {
            postType = "NORMAL";
            pricePerDayUSD = 1.0; // 1$/ngày cho NORMAL
        }
        jobPosting.setPostType(postType);
        // Tính số ngày đăng
        java.time.LocalDateTime start = requestDTO.getCreatedAt() != null ? requestDTO.getCreatedAt() : java.time.LocalDateTime.now();
        java.time.LocalDateTime end = requestDTO.getEndAt();
        long days = 1;
        if (end != null && end.isAfter(start)) {
            days = java.time.Duration.between(start, end).toDays();
            if (days < 1) days = 1;
        }
        double totalUSD = pricePerDayUSD * days;
        long VND_RATE = 26380L;
        Long postPrice = (long)(totalUSD * VND_RATE); // phí backend tính bằng VND
        jobPosting.setPostPrice(postPrice);
        // ...existing code...

        // Kiểm tra số dư trước khi trừ tiền
        Long userId = employer.getUser().getId();
        Long currentBalance = userBalanceService.getBalance(userId);
        if (currentBalance == null || currentBalance < postPrice) {
            throw new com.example.WorkWite_Repo_BE.exceptions.BusinessException("Your balance is not enough to post this job. Please top up your account.");
        }
        // Trừ tiền user khi đăng bài
        userBalanceService.subtractBalance(userId, postPrice);

        // Ghi log
        systemLogService.saveLog(
                userId,
                actor,
                "CREATE_JOB",
                "Employer posted a job: " + jobPosting.getTitle() + " | Type: " + postType + " | Price: " + postPrice,
                "SUCCESS"
        );

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        JobPostingResponseDTO response = mapToResponseDTO(savedJobPosting);
        response.setPostPriceUSD(Double.valueOf(totalUSD)); // Trả về tổng tiền USD cho FE
        return response;
    }

    // Helper: lấy userId từ username
    private Long getUserIdByUsername(String username) {
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

        // Lấy actor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            try {
                java.lang.reflect.Method getEmailMethod = principal.getClass().getMethod("getEmail");
                Object emailObj = getEmailMethod.invoke(principal);
                if (emailObj != null) {
                    actor = emailObj.toString();
                }
            } catch (Exception e) {
                if (principal instanceof UserDetails) {
                    actor = ((UserDetails) principal).getUsername();
                } else {
                    actor = authentication.getName();
                }
            }
        }
        if (actor == null) {
            throw new RuntimeException("Unauthorized: Cannot get actor from token");
        }

        // Kiểm tra quyền sửa
        if (!jobPosting.getEmployer().getUser().getUsername().equals(actor) &&
                (jobPosting.getEmployer().getUser().getEmail() == null
                        || !jobPosting.getEmployer().getUser().getEmail().equals(actor))) {
            throw new RuntimeException("Forbidden: Only the owner employer can update this job posting");
        }

        // Update fields
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

        // Ghi log
        systemLogService.saveLog(
                jobPosting.getEmployer().getUser().getId(),
                actor,
                "UPDATE_JOB",
                "Employer updated job: " + jobPosting.getTitle(),
                "SUCCESS"
        );

        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return mapToResponseDTO(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));

        // Lấy actor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            try {
                java.lang.reflect.Method getEmailMethod = principal.getClass().getMethod("getEmail");
                Object emailObj = getEmailMethod.invoke(principal);
                if (emailObj != null) {
                    actor = emailObj.toString();
                }
            } catch (Exception e) {
                if (principal instanceof UserDetails) {
                    actor = ((UserDetails) principal).getUsername();
                } else {
                    actor = authentication.getName();
                }
            }
        }
        if (actor == null) {
            throw new RuntimeException("Unauthorized: Cannot get actor from token");
        }

        // Kiểm tra quyền xóa
        if (!jobPosting.getEmployer().getUser().getUsername().equals(actor) &&
                (jobPosting.getEmployer().getUser().getEmail() == null
                        || !jobPosting.getEmployer().getUser().getEmail().equals(actor))) {
            throw new RuntimeException("Forbidden: Only the owner employer can delete this job posting");
        }

        jobPostingRepository.deleteById(id);

        // Ghi log
        systemLogService.saveLog(
                jobPosting.getEmployer().getUser().getId(),
                actor,
                "DELETE_JOB",
                "Employer deleted job with id: " + id,
                "SUCCESS"
        );
    }

    private JobPostingResponseDTO mapToResponseDTO(JobPosting jobPosting) {
        JobPostingResponseDTO responseDTO = new JobPostingResponseDTO();
        responseDTO.setId(jobPosting.getId());
        responseDTO.setEmployerId(jobPosting.getEmployer().getId());

        // Lấy tên employer từ user hoặc companyInformation
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
        responseDTO.setPostType(jobPosting.getPostType());
        responseDTO.setPostPrice(jobPosting.getPostPrice());

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

        org.springframework.data.domain.Page<JobPosting> jobPostingsPage =
                jobPostingRepository.findByCategoryContainingAndLocationContainingAndSalaryRangeContainingAndJobTypeContainingAndRequiredDegreeContaining(
                        category != null ? category : "",
                        location != null ? location : "",
                        salaryRange != null ? salaryRange : "",
                        jobType != null ? jobType : "",
                        requiredDegree != null ? requiredDegree : "",
                        pageable
                );

        // Nếu filter requiredSkills, lọc tiếp
        List<JobPosting> filtered = jobPostingsPage.getContent();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            filtered = filtered.stream()
                    .filter(jp -> jp.getRequiredSkills() != null &&
                            jp.getRequiredSkills().stream()
                                    .anyMatch(skill -> skill.toLowerCase().contains(requiredSkills.toLowerCase())))
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