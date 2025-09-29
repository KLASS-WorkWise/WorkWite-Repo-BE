package com.example.WorkWite_Repo_BE.services;
import com.example.WorkWite_Repo_BE.api.RestResponse;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedEmployeeListJobResponseDto;
import com.example.WorkWite_Repo_BE.dtos.savejob.PaginatedSaveJobResponseDto;
import com.example.WorkWite_Repo_BE.dtos.savejob.SavedJobDTO;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.entities.SavedJob;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import com.example.WorkWite_Repo_BE.repositories.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateJpaRepository candidateRepository;
    private final AuthService authService;

    // Map Entity -> DTO
    public SavedJobDTO mapToDTO(SavedJob savedJob) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Map JobPosting -> JobPostingResponseDTO
        JobPostingResponseDTO jobPostingDto = JobPostingResponseDTO.builder()
                .id(savedJob.getJobPosting().getId())
                .title(savedJob.getJobPosting().getTitle())
                .description(savedJob.getJobPosting().getDescription())
                .location(savedJob.getJobPosting().getLocation())
                .jobType(savedJob.getJobPosting().getJobType())
                .category(savedJob.getJobPosting().getCategory())
                .employerName(savedJob.getJobPosting().getEmployer().getCompanyInformation().getCompanyName())
                .requiredDegree(savedJob.getJobPosting().getEmployer().getCompanyInformation().getLogoUrl())

                // thêm các field khác nếu có
                .build();

        return SavedJobDTO.builder()
                .savedJobId(savedJob.getId())
                .JobPostingResponseDTO(jobPostingDto)
                .savedAt(savedJob.getSavedAt().format(formatter))
                .build();
    }

    // Lưu job
    public SavedJobDTO saveJob(Long jobPostingId) {
        Long candidateId = authService.getCurrentUserCandidateId();

        if (savedJobRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã lưu job này rồi");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate không tồn tại"));

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job không tồn tại"));

        SavedJob savedJob = SavedJob.builder()
                .candidate(candidate)
                .jobPosting(jobPosting)
                .build();

        SavedJob saved = savedJobRepository.save(savedJob);
        return mapToDTO(saved); // ✅ Trả DTO
    }

    // Lấy danh sách job đã lưu
//    public List<SavedJobDTO> getMySavedJobs() {
//        Long candidateId = authService.getCurrentUserCandidateId();
//        return savedJobRepository.findByCandidateId(candidateId) // ✅ dùng repo, không cần candidate.getSavedJobs()
//                .stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }

    public RestResponse<PaginatedSaveJobResponseDto<SavedJobDTO>> getMySavedJobs(
            int page, int size, String sortBy, String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Long currentCandidateId = authService.getCurrentUserCandidateId();

        Page<SavedJob> savedJobs = savedJobRepository.findByCandidateId(currentCandidateId, pageable);

        List<SavedJobDTO> savedJobDTOSDtos = savedJobs.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        PaginatedSaveJobResponseDto<SavedJobDTO> pagedData = PaginatedSaveJobResponseDto.<SavedJobDTO>builder()
                .content(savedJobDTOSDtos)
                .pageNumber(savedJobs.getNumber())
                .pageSize(savedJobs.getSize())
                .totalRecords(savedJobs.getTotalElements())
                .totalPages(savedJobs.getTotalPages())
                .hasNext(savedJobs.hasNext())
                .hasPrevious(savedJobs.hasPrevious())
                .build();
        return RestResponse.<PaginatedSaveJobResponseDto<SavedJobDTO>>builder()
                .statusCode(200)
                .error(null)
                .message("Success")
                .data(pagedData)
                .build();
    }

    // Xóa job đã lưu
    public void removeSavedJob(Long id) {
        Long candidateId = authService.getCurrentUserCandidateId();
        SavedJob savedJob = savedJobRepository.findByCandidateIdAndId(candidateId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy job đã lưu"));

        savedJobRepository.delete(savedJob);
    }
}

