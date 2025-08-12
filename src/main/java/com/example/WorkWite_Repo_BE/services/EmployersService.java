package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.CreateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.PaginatedEmployerRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.UpdateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.CompanyInformationJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployersService {
    private final EmployersJpaRepository employersJpaRepository;
    private final CompanyInformationJpaRepository companyInformationRepository;

    public EmployersService(EmployersJpaRepository employersJpaRepository,
            CompanyInformationJpaRepository companyInformationRepository) {
        this.employersJpaRepository = employersJpaRepository;
        this.companyInformationRepository = companyInformationRepository;
    }

    public EmployerResponseDto convertDto(Employers employers) {
        return new EmployerResponseDto(
                employers.getId(),
                employers.getUser().getUsername(),
                employers.getUser().getEmail(),
                employers.getUser().getFullName(),
                employers.getUser().getStatus(),
                employers.getPhoneNumber(),
                employers.getAvatar());
    }

    @Transactional
    public EmployerResponseDto updateEmployerProfile(Long id, UpdateEmployerRequestDto dto) {
        Employers employer = employersJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Update User
        User user = employer.getUser();
        user.setUsername(dto.getUsername());
        // Nếu cần mã hoá mật khẩu:
        // user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setStatus(dto.getStatus());

        // Update Employers
        employer.setPhoneNumber(dto.getPhoneNumber());
        employer.setAvatar(dto.getAvatar());

        Employers savedEmployer = employersJpaRepository.save(employer);

        return convertDto(savedEmployer);
    }

}