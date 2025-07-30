package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CreatCandidateRequest;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.PaginatedCandidateResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.UpdateCandidateRequestDto;
import com.example.WorkWite_Repo_BE.entities.Candidates;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidatesServices {
    private final CandidateJpaRepository candidateJpaRepository;

    public CandidatesServices(CandidateJpaRepository candidateJpaRepository) {
        this.candidateJpaRepository = candidateJpaRepository;
    }

    private CandidatesResponseDto convertToDto(Candidates candidate) {
        return new CandidatesResponseDto(
                candidate.getId(),
                candidate.getPhone(),
                candidate.getAddress(),
                candidate.getBio(),
                candidate.getExperience(),
                candidate.getEducation(),
                candidate.getSkills()
        );
    }

    // lay tat ca candidates
    public List<CandidatesResponseDto> getAllCandidates() {
        List<Candidates> candidates = this.candidateJpaRepository.findAll();
        return candidates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    // lay candidate theo id
     public CandidatesResponseDto getCandidateById(Long id) {
        Candidates candidate = this.candidateJpaRepository.findById(id).orElse(null);
        return convertToDto(candidate);
     }

     // creat candidates
    public CandidatesResponseDto creatCandidate(CreatCandidateRequest creatCandidateRequest) {
        Candidates candidate = new Candidates();
        candidate.setPhone(creatCandidateRequest.getPhone());
        candidate.setAddress(creatCandidateRequest.getAddress());
        candidate.setBio(creatCandidateRequest.getBio());
        candidate.setExperience(creatCandidateRequest.getExperience());
        candidate.setEducation(creatCandidateRequest.getEducation());
        candidate.setSkills(creatCandidateRequest.getSkills());

        // fix o day nha : chua save
        Candidates savedCandidate = candidateJpaRepository.save(candidate);
        return convertToDto(savedCandidate);
    }
    //cap nhat candidate
    public CandidatesResponseDto updateCandidateById(Long id, UpdateCandidateRequestDto updateCandidateRequest) {
        Candidates candidate = this.candidateJpaRepository.findById(id).orElse(null);

        candidate.setPhone(updateCandidateRequest.getPhone());
        candidate.setAddress(updateCandidateRequest.getAddress());
        candidate.setBio(updateCandidateRequest.getBio());
        candidate.setExperience(updateCandidateRequest.getExperience());
        candidate.setSkills(updateCandidateRequest.getSkills());
        Candidates update = this.candidateJpaRepository.save(candidate);
        return convertToDto(update);
    }
    // xoa candidate
    public void deletaCandidateById(Long id) {
        this.candidateJpaRepository.findById(id).orElse(null);
        this.candidateJpaRepository.deleteById(id);
    }

    // phan trang
    // Phân trang candidate
    public PaginatedCandidateResponseDto getCandidatesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Candidates> candidatePage = this.candidateJpaRepository.findAll(pageable);

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
