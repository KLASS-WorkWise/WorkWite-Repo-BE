package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CreateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.PaginatedCompanyInformationRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.UpdateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.repositories.CompanyInformationJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyInformationService {
    private final CompanyInformationJpaRepository companyInformationJpaRepository;

    public CompanyInformationService(CompanyInformationJpaRepository companyInformationJpaRepository, EmployersJpaRepository employersRepository) {
        this.companyInformationJpaRepository = companyInformationJpaRepository;
        this.employersRepository = employersRepository;
    }
    private CompanyInformationReponseDto convertToDto(CompanyInformation companyInformationReponseDto) {
        return new CompanyInformationReponseDto(
                companyInformationReponseDto.getId(),
                companyInformationReponseDto.getEmployee(),
                companyInformationReponseDto.getCompanyName(),
                companyInformationReponseDto.getLogoUrl(),
                companyInformationReponseDto.getBannerUrl(),
                companyInformationReponseDto.getEmail(),
                companyInformationReponseDto.getPhone(),
                companyInformationReponseDto.getDescription(),
                companyInformationReponseDto.getLastPosted(),
                companyInformationReponseDto.getAddress(),
                companyInformationReponseDto.getLocation(),
                companyInformationReponseDto.getWebsite(),
                companyInformationReponseDto.getIndustry()

        );
    }

    public PaginatedCompanyInformationRespondeDto getAllCompanies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<CompanyInformation> companyInformationPage = companyInformationJpaRepository.findAll(pageable);

        List<CompanyInformationReponseDto> companyInformationDto = companyInformationPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedCompanyInformationRespondeDto.builder()
                .data(companyInformationDto)
                .pageNumber(companyInformationPage.getNumber())
                .pageSize(companyInformationPage.getSize())
                .totalPages(companyInformationPage.getTotalPages())
                .totalRecords(companyInformationPage.getTotalElements())
                .hasNext(companyInformationPage.hasNext())
                .hasPrevious(companyInformationPage.hasPrevious())
                .build();
    }
    // Lấy công ty theo ID
    public CompanyInformationReponseDto getCompanyById(Long id) {
        CompanyInformation companyInfo = companyInformationJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return convertToDto(companyInfo);
    }

    private final EmployersJpaRepository employersRepository;

    @Transactional
    public CompanyInformationReponseDto createCompanyInformation(Long employerId, CreateCompanyInformationRequesDto dto) {
        Employers employer = employersRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + employerId));

        CompanyInformation companyInfo = CompanyInformation.builder()
                .employee(dto.getEmployee())
                .companyName(dto.getCompanyName())
                .logoUrl(dto.getLogoUrl())
                .bannerUrl(dto.getBannerUrl())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .description(dto.getDescription())
                .lastPosted(dto.getLastPosted() != null ? dto.getLastPosted() : LocalDateTime.now())
                .address(dto.getAddress())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .industry(dto.getIndustry())
                .build();

        // Gắn quan hệ
        companyInfo.setEmployer(employer);
        employer.setCompanyInformation(companyInfo);

        // Lưu employer (sẽ cascade lưu luôn companyInfo)
        employersRepository.save(employer);

        // Trả về DTO
        return convertToDto(companyInfo);
    }
    public void deleteCompany(Long id) {
        this.companyInformationJpaRepository.deleteById(id);
    }
    public CompanyInformationReponseDto getCompanyInformation(Long id) {

        CompanyInformation companyInformation = this.companyInformationJpaRepository.findById(id).orElse(null);

        return convertToDto(companyInformation);
    }

    @Transactional
    public CompanyInformationReponseDto patchCompanyInformation(Long employerId, UpdateCompanyInformationRequesDto dto) {
        Employers employer = employersRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + employerId));

        CompanyInformation companyInfo = employer.getCompanyInformation();
        if (companyInfo == null) {
            throw new RuntimeException("Company information not found for employer with id: " + employerId);
        }

        if (dto.getEmployee() != null) companyInfo.setEmployee(dto.getEmployee());
        if (dto.getCompanyName() != null) companyInfo.setCompanyName(dto.getCompanyName());
        if (dto.getLogoUrl() != null) companyInfo.setLogoUrl(dto.getLogoUrl());
        if (dto.getBannerUrl() != null) companyInfo.setBannerUrl(dto.getBannerUrl());
        if (dto.getEmail() != null) companyInfo.setEmail(dto.getEmail());
        if (dto.getPhone() != null) companyInfo.setPhone(dto.getPhone());
        if (dto.getDescription() != null) companyInfo.setDescription(dto.getDescription());
        if (dto.getLastPosted() != null) companyInfo.setLastPosted(dto.getLastPosted());
        if (dto.getAddress() != null) companyInfo.setAddress(dto.getAddress());
        if (dto.getLocation() != null) companyInfo.setLocation(dto.getLocation());
        if (dto.getWebsite() != null) companyInfo.setWebsite(dto.getWebsite());
        if (dto.getIndustry() != null) companyInfo.setIndustry(dto.getIndustry());

        CompanyInformation savedInfo = companyInformationJpaRepository.save(companyInfo);

        return convertToDto(savedInfo);
    }

    public PaginatedCompanyInformationRespondeDto searchCompaniesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("companyName").ascending());
        Page<CompanyInformation> result = companyInformationJpaRepository.findByCompanyNameContainingIgnoreCase(name, pageable);

        List<CompanyInformationReponseDto> companyInformationDto = result.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedCompanyInformationRespondeDto.builder()
                .data(companyInformationDto)
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalPages(result.getTotalPages())
                .totalRecords(result.getTotalElements())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .build();
    }




}