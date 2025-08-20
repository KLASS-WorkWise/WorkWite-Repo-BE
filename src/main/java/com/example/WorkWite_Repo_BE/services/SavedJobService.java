package com.example.movie.services;

import com.example.movie.dtos.savejob.SavedJobDTO;
import com.example.movie.entities.Candidate;
import com.example.movie.entities.JobPosting;
import com.example.movie.entities.SavedJob;
import com.example.movie.repositories.CandidateJpaRepository;
import com.example.movie.repositories.JobPostingRepository;
import com.example.movie.repositories.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        return SavedJobDTO.builder()
                .savedJobId(savedJob.getId())
                .jobId(savedJob.getJobPosting().getId())
//                .companyName(savedJob.getJobPosting().getCompany().getName())
                // Nếu JobPosting có field title + location thì mở comment ra
                .jobTitle(savedJob.getJobPosting().getJobTitle())
                //.location(savedJob.getJobPosting().getLocation())
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
    public void removeSavedJob(Long jobPostingId) {
        Long candidateId = authService.getCurrentUserCandidateId();
        SavedJob savedJob = savedJobRepository.findByCandidateIdAndJobPostingId(candidateId, jobPostingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy job đã lưu"));

        savedJobRepository.delete(savedJob);
    }
}
