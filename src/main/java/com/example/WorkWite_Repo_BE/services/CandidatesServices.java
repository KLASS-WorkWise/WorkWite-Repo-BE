package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.AppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CreatCandidateRequest;
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
public class CandidatesServices {
    private final CandidateJpaRepository candidateJpaRepository;

    public CandidatesServices(CandidateJpaRepository candidateJpaRepository) {
        this.candidateJpaRepository = candidateJpaRepository;
    }

    private CandidatesResponseDto convertToDto(Candidate candidate) {
        // Chuyển đổi LocalDateTime thành String với định dạng mong muốn
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
                candidate.getUser(),
                savedJobs,
                resumes);
    }

    // Phương thức tạo Candidate khi người dùng đăng ký
    public void createCandidateForUser(User user) {
        // Tạo mới Candidate
        Candidate candidate = new Candidate();
        candidate.setUser(user);  // Liên kết Candidate với User

        // Lưu Candidate vào cơ sở dữ liệu
        candidateJpaRepository.save(candidate);
    }

    // lay tat ca candidates
    public List<CandidatesResponseDto> getAllCandidates() {
        List<Candidate> candidates = this.candidateJpaRepository.findAll();
        return candidates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    // lay candidate theo id
     public CandidatesResponseDto getCandidateById(Long id) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        return convertToDto(candidate);
     }


    //cap nhat candidate
    public CandidatesResponseDto updateCandidateById(Long id, UpdateCandidateRequestDto updateCandidateRequest) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        if(updateCandidateRequest.getUser() != null) {
            User user = updateCandidateRequest.getUser();

            candidate.getUser().setEmail(user.getEmail());
            candidate.getUser().setPhone(user.getPhone());
        }

        Candidate update = this.candidateJpaRepository.save(candidate);
        return convertToDto(update);
    }
    // xoa candidate
//    public void deletaCandidateById(Long id) {
//        this.candidateJpaRepository.findById(id).orElse(null);
//        this.candidateJpaRepository.deleteById(id);
//    }

    // phan trang
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
