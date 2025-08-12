package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CreateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.PaginatedCompanyInformationRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.UpdateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.repositories.CompanyInformationJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyInformationService {
    private final CompanyInformationJpaRepository companyInformationJpaRepository;

    public CompanyInformationService(CompanyInformationJpaRepository companyInformationJpaRepository) {
        this.companyInformationJpaRepository = companyInformationJpaRepository;
    }

    private CompanyInformationReponseDto convertToDto(CompanyInformation companyInformationReponseDto) {
        return new CompanyInformationReponseDto(
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

    public CompanyInformationReponseDto create(CreateCompanyInformationRequesDto dto) {
        CompanyInformation companyInfo = new CompanyInformation();
        companyInfo.setEmployee(dto.getEmployee());
        companyInfo.setCompanyName(dto.getCompanyName());
        companyInfo.setLogoUrl(dto.getLogoUrl());
        companyInfo.setBannerUrl(dto.getBannerUrl());
        companyInfo.setEmail(dto.getEmail());
        companyInfo.setPhone(dto.getPhone());
        companyInfo.setDescription(dto.getDescription());
        companyInfo.setLastPosted(dto.getLastPosted());
        companyInfo.setAddress(dto.getAddress());
        companyInfo.setLocation(dto.getLocation());
        companyInfo.setWebsite(dto.getWebsite());
        companyInfo.setIndustry(dto.getIndustry());

        CompanyInformation companyInformation = this.companyInformationJpaRepository.save(companyInfo);

        return convertToDto(companyInformation);
    }

    public void deleteCompany(Long id) {
        this.companyInformationJpaRepository.deleteById(id);
    }

    public CompanyInformationReponseDto getCompanyInformation(Long id) {

        CompanyInformation companyInformation = this.companyInformationJpaRepository.findById(id).orElse(null);

        return convertToDto(companyInformation);
    }

    public CompanyInformationReponseDto updateCompany(Long id,
            UpdateCompanyInformationRequesDto companyInformationReponseDto) {
        CompanyInformation companyInformation = this.companyInformationJpaRepository.findById(id).orElse(null);
        assert companyInformation != null;
        companyInformation.setEmployee(companyInformationReponseDto.getEmployee());
        companyInformation.setCompanyName(companyInformationReponseDto.getCompanyName());
        companyInformation.setLogoUrl(companyInformationReponseDto.getLogoUrl());
        companyInformation.setBannerUrl(companyInformationReponseDto.getBannerUrl());
        companyInformation.setEmail(companyInformationReponseDto.getEmail());
        companyInformation.setPhone(companyInformationReponseDto.getPhone());
        companyInformation.setDescription(companyInformationReponseDto.getDescription());
        companyInformation.setLastPosted(companyInformationReponseDto.getLastPosted());
        companyInformation.setAddress(companyInformationReponseDto.getAddress());
        companyInformation.setLocation(companyInformationReponseDto.getLocation());
        companyInformation.setWebsite(companyInformationReponseDto.getWebsite());
        companyInformation.setIndustry(companyInformationReponseDto.getIndustry());

        CompanyInformation updatedCompanyInformation = this.companyInformationJpaRepository.save(companyInformation);

        return convertToDto(updatedCompanyInformation);
    }

}