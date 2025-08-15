package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantRequestDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.ApplicantResponseDto;
import com.example.WorkWite_Repo_BE.dtos.applicant.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.repositories.AppJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppService {

    private final AppJpaRepository appJpaRepository;

    public AppService(AppJpaRepository appJpaRepository) {
        this.appJpaRepository = appJpaRepository;
    }

    private ApplicantRequestDto convertToDto(Applicant app) {
        return new ApplicantRequestDto(
                app.getId(),
                app.getJobPosting() != null
                        ? (app.getJobPosting().getId() != null ? app.getJobPosting().getId().intValue() : null)
                        : null,
                app.getCandidate() != null ? Long.valueOf(app.getCandidate().getId()).intValue() : null,
                app.getCoverLetter(),
                app.getStatus(),
                app.getAppliedAt());
    }

    public List<ApplicantRequestDto> getAllApps() {
        List<Applicant> apps = this.appJpaRepository.findAll();
        return apps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PaginatedAppResponseDto getAllAppsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Applicant> appPage = this.appJpaRepository.findAll(pageable);
        List<ApplicantRequestDto> appDtos = appPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedAppResponseDto.builder()
                .data(appDtos)
                .pageNumber(appPage.getNumber())
                .pageSize(appPage.getSize())
                .totalRecords(appPage.getTotalElements())
                .totalPages(appPage.getTotalPages())
                .hasNext(appPage.hasNext())
                .hasPrevious(appPage.hasPrevious())
                .build();

    }

    public ApplicantRequestDto getAppById(Integer id) {
        Applicant app = this.appJpaRepository.findById(id).orElseThrow();
        return convertToDto(app);
    }

    public ApplicantRequestDto createApplication(ApplicantResponseDto createApplicationRequestDto) {

        Applicant app = new Applicant();

        app.setCandidate(createApplicationRequestDto.getCandidate());
        app.setCoverLetter(createApplicationRequestDto.getCoverLetter());
        app.setStatus(createApplicationRequestDto.getStatus());
        app.setAppliedAt(createApplicationRequestDto.getAppliedAt());

        Applicant createApplicant = this.appJpaRepository.save(app);
        return convertToDto(createApplicant);
    }

    public ApplicantRequestDto updateApplication(Integer id, UpdateAppRequestDto application) {
        Applicant existingApp = this.appJpaRepository.findById(id).orElseThrow();

        existingApp.setCandidate(application.getCandidate());
        existingApp.setCoverLetter(application.getCoverLetter());
        existingApp.setStatus(application.getStatus());
        existingApp.setAppliedAt(application.getAppliedAt());
        Applicant updateApplicant = this.appJpaRepository.save(existingApp);
        return convertToDto(updateApplicant);
    }

    public void deleteApplication(Integer id) {
        this.appJpaRepository.findById(id).orElseThrow();
        this.appJpaRepository.deleteById(id);
    }

}
