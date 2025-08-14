package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.PaginatedCandidateResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.UpdateCandidateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ResumeDto.ResumeResponseDto;
import com.example.WorkWite_Repo_BE.dtos.SavedJobDto.SaveJobResponseDto;
import com.example.WorkWite_Repo_BE.entities.Candidate;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                .map(resume -> {
                    String createdAtStr = null;
                    if (resume.getCreatedAt() != null) {
                        createdAtStr = resume.getCreatedAt().format(formatter);
                    }
                    return new ResumeResponseDto(
                            resume.getId(),
                            resume.getProfilePicture(),
                            resume.getFullName(),
                            resume.getEmail(),
                            resume.getPhone(),
                            createdAtStr,
                            resume.getJobTitle(),
                            resume.getActivities() == null ? java.util.Collections.emptyList() : resume.getActivities(),
                            resume.getEducations() == null ? java.util.Collections.emptyList() : resume.getEducations(),
                            resume.getAwards() == null ? java.util.Collections.emptyList() : resume.getAwards(),
                            resume.getApplications() == null ? java.util.Collections.emptyList() : resume.getApplications(),
                            resume.getSkill() == null ? java.util.Collections.emptyList() : resume.getSkill(),
                            resume.getSummary()
                    );
                })
                .collect(Collectors.toList());
        // con code cua aplication
        return new CandidatesResponseDto(
                candidate.getId(),
                candidate.getUser(),
                candidate.getPhoneNumber(),
                candidate.getAvatar(),
                savedJobs,
                resumes);
    }

    // Phương thức tạo Candidate khi người dùng đăng ký
    public void createCandidateForUser(User user) {
        // Tạo mới Candidate
        Candidate candidate = new Candidate();
        candidate.setUser(user);  // Liên kết Candidate với User
        candidate.setResumes(new ArrayList<>());
        candidate.setApplications(new ArrayList<>());
        candidate.setSavedJobs(new ArrayList<>());

        // Lưu Candidate vào cơ sở dữ liệu
        candidateJpaRepository.save(candidate);
    }

    // Lấy tất cả candidate có role Users
    public List<CandidatesResponseDto> getAllCandidates() {
        List<Candidate> candidates = this.candidateJpaRepository.findAllCandidatesWithUserRole();
        return candidates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Lấy candidate theo id, chỉ trả về nếu có role Users
    public CandidatesResponseDto getCandidateById(Long id) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        if (candidate == null) return null;
        boolean isUserRole = candidate.getUser().getRoles().stream()
                .anyMatch(role -> "Users".equals(role.getName()));
        if (!isUserRole) return null;
        return convertToDto(candidate);
    }

    //cap nhat candidate
    public CandidatesResponseDto updateCandidateById(Long id, UpdateCandidateRequestDto updateCandidateRequest) {
        Candidate candidate = this.candidateJpaRepository.findById(id).orElse(null);
        if(candidate != null) {
            candidate.getUser().setEmail(updateCandidateRequest.getEmail());
            candidate.getUser().setFullName(updateCandidateRequest.getFullName());
            candidate.setPhoneNumber(updateCandidateRequest.getPhoneNumber());
            candidate.setAvatar(updateCandidateRequest.getAvatar());
            Candidate updatedCandidate = this.candidateJpaRepository.save(candidate);
            return convertToDto(updatedCandidate);
        }
        return null;
    }

    // Phân trang candidate có role Users
    public PaginatedCandidateResponseDto getCandidatesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Candidate> candidatePage = this.candidateJpaRepository.findAllCandidatesWithUserRole(pageable);
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
