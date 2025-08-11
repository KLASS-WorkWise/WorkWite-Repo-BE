package com.example.WorkWite_Repo_BE.services;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.PaginatedCandidateResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.UpdateCandidateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SavedJobDto.SaveJobResponseDto;
import com.example.WorkWite_Repo_BE.entities.*;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CandidatesServices_v1 {
    private final CandidateJpaRepository candidateJpaRepository;

    public CandidatesServices_v1(CandidateJpaRepository candidateJpaRepository) {
        this.candidateJpaRepository = candidateJpaRepository;
    }

    private CandidatesResponseDto convertToDto(Candidate candidate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<SaveJobResponseDto> savedJobs = candidate.getSavedJobs().stream()
                .map(savedJob -> new SaveJobResponseDto(
                        savedJob.getId(),
                        savedJob.getJobPosting().getId(),
                        savedJob.getSavedAt().format(formatter)))
                .collect(Collectors.toList());

        List<ResumeResponseDto> resumes = candidate.getResumes().stream()
                .map(resume -> new ResumeResponseDto(
                        resume.getId(),
                        resume.getProfilePicture(),
                        resume.getFullName(),
                        resume.getEmail(),
                        resume.getPhone(),
                        resume.getCreatedAt().format(formatter),
                        resume.getSummary()))
                .collect(Collectors.toList());

        return new CandidatesResponseDto(
                candidate.getId(),
                candidate.getEmail(),
                candidate.getUsername(),
                candidate.getName(),
                candidate.getAvatar(),
                candidate.getPhoneNumber(),
                savedJobs,
                resumes);
    }

    // Cập nhật candidate
    public CandidatesResponseDto updateCandidateById(Long id, UpdateCandidateRequestDto updateCandidateRequest) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        // Kiểm tra xem Candidate có tồn tại không
        if (candidate == null) {
            return null;  // Hoặc có thể ném exception như EntityNotFoundException
        }

        // Cập nhật thông tin cho Candidate
        candidate.setUsername(updateCandidateRequest.getUsername());
        candidate.setEmail(updateCandidateRequest.getEmail());
        candidate.setPhoneNumber(updateCandidateRequest.getPhoneNumber());
        candidate.setName(updateCandidateRequest.getName());
        candidate.setAvatar(updateCandidateRequest.getAvatar());

        // Lưu lại Candidate đã được cập nhật
        Candidate updatedCandidate = this.candidateJpaRepository.save(candidate);
        // Trả về DTO sau khi cập nhật
        return convertToDto(updatedCandidate);
    }
    // lấy theo id
    public CandidatesResponseDto getCandidateById(Long id) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        if (candidate == null) {
            return null;
        }
        return convertToDto(candidate);
    }

    // Phân trang candidate
    public PaginatedCandidateResponseDto getCandidatesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Candidate> candidatePage = this.candidateJpaRepository.findAll(pageable);

        List<CandidatesResponseDto> dtos = candidatePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedCandidateResponseDto.builder()
                .data(dtos)
                .pageNumber(candidatePage.getNumber())
                .pageSize(candidatePage.getSize())
                .totalRecords((int) candidatePage.getTotalElements())
                .totalPages(candidatePage.getTotalPages())
                .hasNext(candidatePage.hasNext())
                .hasPrevious(candidatePage.hasPrevious())
                .build();
    }
}


