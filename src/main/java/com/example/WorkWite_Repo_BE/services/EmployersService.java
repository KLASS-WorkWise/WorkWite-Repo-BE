package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.CreateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.PaginatedEmployerRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.UpdateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployersService {
    private final EmployersJpaRepository employersJpaRepository;


    public EmployersService(EmployersJpaRepository employersJpaRepository) {
        this.employersJpaRepository = employersJpaRepository;
    }

    private EmployerResponseDto convertToDto(Employers employers) {
        return new EmployerResponseDto(
                employers.getUsername(),
                employers.getPassword(),
                employers.getFullName(),
                employers.getEmail(),
                employers.getPhoneNumber(),
                employers.getAvatar(),
                employers.getStatus()
        );
    }

    public PaginatedEmployerRespondeDto getAllEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Employers> employersPage = this.employersJpaRepository.findAllEmployersWithRole(pageable);

        List<EmployerResponseDto> employerDto = employersPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PaginatedEmployerRespondeDto.builder()
                .data(employerDto)
                .pageNumber(employersPage.getNumber())
                .pageSize(employersPage.getSize())
                .totalPages(employersPage.getTotalPages())
                .totalRecords(employersPage.getTotalElements())
                .hasNext(employersPage.hasNext())
                .hasPrevious(employersPage.hasPrevious())
                .build();
    }
    public EmployerResponseDto createEmployer(CreateEmployerRequestDto employerResponseDto) {
        Employers employers = new Employers();
        employers.setUsername(employerResponseDto.getUsername());
        employers.setPassword(employerResponseDto.getPassword());
        employers.setFullName(employerResponseDto.getFullName());
        employers.setEmail(employerResponseDto.getEmail());
        employers.setPhoneNumber(employerResponseDto.getPhoneNumber());
        employers.setAvatar(employerResponseDto.getAvatar());

        Employers createdEmployer = this.employersJpaRepository.save(employers);

        return convertToDto(createdEmployer);
    }

    public EmployerResponseDto getEmployerById(Long id) {
        Employers employer = this.employersJpaRepository.findById(id).orElse(null);

        return convertToDto(employer);
    }

    public void deleteEmployerById(Long id) {
        this.employersJpaRepository.deleteById(id);
    }
    public EmployerResponseDto updateEmployerById(Long id, UpdateEmployerRequestDto employerResponseDto) {
        Employers employers = this.employersJpaRepository.findById(id).orElse(null);
        assert  employers != null;
        employers.setUsername(employerResponseDto.getUsername());
        employers.setPassword(employerResponseDto.getPassword());
        employers.setFullName(employerResponseDto.getFullName());
        employers.setEmail(employerResponseDto.getEmail());
        employers.setPhoneNumber(employerResponseDto.getPhoneNumber());
        employers.setAvatar(employerResponseDto.getAvatar());

        Employers updatedEmployer = this.employersJpaRepository.save(employers);

        return convertToDto(updatedEmployer);

    }
}
