package com.example.WorkWite_Repo_BE.services;
import com.example.WorkWite_Repo_BE.dtos.JobPostDto.JobPostingResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.savejob.SavedJobDTO;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.JobPosting;
import com.example.WorkWite_Repo_BE.entities.SavedJob;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import com.example.WorkWite_Repo_BE.repositories.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public List<SavedJobDTO> getMySavedJobs() {
        Long candidateId = authService.getCurrentUserCandidateId();
        return savedJobRepository.findByCandidateId(candidateId) // ✅ dùng repo, không cần candidate.getSavedJobs()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Xóa job đã lưu
    public void removeSavedJob(Long id) {
        Long candidateId = authService.getCurrentUserCandidateId();
        SavedJob savedJob = savedJobRepository.findByCandidateIdAndId(candidateId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy job đã lưu"));

        savedJobRepository.delete(savedJob);
    }
}

