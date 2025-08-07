//package com.example.WorkWite_Repo_BE.services;
//
//import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
//import com.example.WorkWite_Repo_BE.dtos.EmployersDto.PaginatedEmployerRespondeDto;
//import com.example.WorkWite_Repo_BE.entities.Employers;
//import com.example.WorkWite_Repo_BE.entities.User;
//import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
//import com.example.WorkWite_Repo_BE.repositories.UserJpaResponsitory;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class EmployersService {
//    private final EmployersJpaRepository employersJpaRepository;
//    private final UserJpaResponsitory userJpaResponsitory;
//
//    public EmployersService(EmployersJpaRepository employersJpaRepository, UserJpaResponsitory userJpaResponsitory) {
//        this.employersJpaRepository = employersJpaRepository;
//        this.userJpaResponsitory = userJpaResponsitory;
//    }
//
//    private EmployerResponseDto convertToDto(Employers employers) {
//        String userId = employers.getUser() != null ? employers.getUser().getId() : null;
//        return new EmployerResponseDto(
//                userId,
//                employers.getCompanyName(),
//                employers.getWebsite(),
//                employers.getLogoUrl(),
//                employers.getAddress(),
//                employers.getEmail(),
//                employers.getIndustry(),
//                employers.getCulture(),
//                employers.getBenefits(),
//                employers.getMediaUrls());
//    }
//
//    public PaginatedEmployerRespondeDto getAllEmployers(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<Employers> employersPage = this.employersJpaRepository.findAll(pageable);
//
//        List<EmployerResponseDto> employerDto = employersPage.getContent().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//
//        return PaginatedEmployerRespondeDto.builder()
//                .data(employerDto)
//                .pageNumber(employersPage.getNumber())
//                .pageSize(employersPage.getSize())
//                .totalPages(employersPage.getTotalPages())
//                .totalRecords(employersPage.getTotalElements())
//                .hasNext(employersPage.hasNext())
//                .hasPrevious(employersPage.hasPrevious())
//                .build();
//    }
//
//    public EmployerResponseDto createEmployer(EmployerResponseDto employerResponseDto) {
//        // Lấy user từ userId truyền lên DTO
//        User user = userJpaResponsitory.findById(employerResponseDto.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + employerResponseDto.getUserId()));
//        Employers employers = new Employers();
//        employers.setCompanyName(employerResponseDto.getCompanyName());
//        employers.setWebsite(employerResponseDto.getWebsite());
//        employers.setLogoUrl(employerResponseDto.getLogo_url());
//        employers.setAddress(employerResponseDto.getAddress());
//        employers.setEmail(employerResponseDto.getEmail());
//        employers.setIndustry(employerResponseDto.getIndustry());
//        employers.setCulture(employerResponseDto.getCulture());
//        employers.setBenefits(employerResponseDto.getBenefits());
//        employers.setMediaUrls(employerResponseDto.getMedia_url());
//        // Gán user cho employer
//        employers.setUser(user);
//
//        Employers createdEmployer = this.employersJpaRepository.save(employers);
//
//        return convertToDto(createdEmployer);
//    }
//
//    public EmployerResponseDto getEmployerById(Long id) {
//        Employers employer = this.employersJpaRepository.findById(id).orElse(null);
//
//        return convertToDto(employer);
//    }
//
//    public void deleteEmployerById(Long id) {
//        this.employersJpaRepository.deleteById(id);
//    }
//
//    public EmployerResponseDto updateEmployerById(Long id, EmployerResponseDto employerResponseDto) {
//        Employers employer = this.employersJpaRepository.findById(id).orElse(null);
//        assert employer != null;
//        employer.setWebsite(employerResponseDto.getWebsite());
//        employer.setLogoUrl(employerResponseDto.getLogo_url());
//        employer.setAddress(employerResponseDto.getAddress());
//        employer.setEmail(employerResponseDto.getEmail());
//        employer.setIndustry(employerResponseDto.getIndustry());
//        employer.setCulture(employerResponseDto.getCulture());
//        employer.setBenefits(employerResponseDto.getBenefits());
//        employer.setMediaUrls(employerResponseDto.getMedia_url());
//        employer.setCompanyName(employerResponseDto.getCompanyName());
//
//        Employers updatedEmployer = this.employersJpaRepository.save(employer);
//
//        return convertToDto(updatedEmployer);
//
//    }
//}
