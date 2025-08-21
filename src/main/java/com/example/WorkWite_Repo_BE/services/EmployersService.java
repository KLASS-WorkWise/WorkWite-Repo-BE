package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.*;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.CompanyInformationJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;

import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
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
    private final UserJpaRepository userJpaRepository;
    private final RoleService roleService;

    public EmployersService(EmployersJpaRepository employersJpaRepository,
                            CompanyInformationJpaRepository companyInformationRepository, UserJpaRepository userJpaRepository, RoleService roleService) {
        this.employersJpaRepository = employersJpaRepository;
        this.companyInformationRepository = companyInformationRepository;
        this.userJpaRepository = userJpaRepository;
        this.roleService = roleService;
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
    public UpgradeEmployerResponseDto upgradeDto(Employers employers) {
        return new UpgradeEmployerResponseDto(
                employers.getId(),
                employers.getUser().getUsername(),
                employers.getUser().getEmail(),
                employers.getUser().getFullName(),
                employers.getPhoneNumber(),
                employers.getCompanyInformation()
        );
    }

    // getByid
    public EmployerResponseDto getEmployerById(Long id) {
        Employers employer = employersJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + id));

        return convertDto(employer);
    }

    // Get tất cả employer đã gửi yêu cầu

    public PaginatedEmployerRespondeDto getPaginatedUpgradeEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Employers> employersPage = employersJpaRepository.findByStatus("PENDING",pageable);
        List<UpgradeEmployerResponseDto> upgradeEmployerResponseDtos = employersPage.getContent().stream()
                .map(this::upgradeDto)
                .collect(Collectors.toList());

        return PaginatedEmployerRespondeDto.builder()
                .data(upgradeEmployerResponseDtos)
                .pageNumber(employersPage.getNumber())
                .pageSize(employersPage.getSize())
                .totalRecords(employersPage.getTotalElements())
                .totalPages(employersPage.getTotalPages())
                .hasNext(employersPage.hasNext())
                .hasPrevious(employersPage.hasPrevious())
                .build();
    }

    // Get tất cả employer đã duyệt
    public PaginatedEmployerRespondeDto getPaginatedEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Employers> employersPage = employersJpaRepository.findByStatus("APPROVED",pageable);
        List<EmployerResponseDto> employerResponseDtos = employersPage.getContent().stream()
                .map(this::convertDto)
                .collect(Collectors.toList());

        return PaginatedEmployerRespondeDto.builder()
                .data(employerResponseDtos)
                .pageNumber(employersPage.getNumber())
                .pageSize(employersPage.getSize())
                .totalRecords(employersPage.getTotalElements())
                .totalPages(employersPage.getTotalPages())
                .hasNext(employersPage.hasNext())
                .hasPrevious(employersPage.hasPrevious())
                .build();
    }
    // update
    @Transactional
    public EmployerResponseDto updateEmployerProfile(Long id, UpdateEmployerRequestDto dto) {
        Employers employer = employersJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Update User
        User user = employer.getUser();
        // user.setUsername(dto.getUsername());
        // Nếu cần mã hoá mật khẩu:
        // user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        // user.setStatus(dto.getStatus());

        // Update Employers
        employer.setPhoneNumber(dto.getPhoneNumber());
        employer.setAvatar(dto.getAvatar());

        Employers savedEmployer = employersJpaRepository.save(employer);

        return convertDto(savedEmployer);
    }

    // người dùng gửi yêu cầu nâng cấp employer
    @Transactional
    public void sendUpgradeRequest(Long userId, UpgradeEmployerRequestDto dto) {
        // 1. lấy user
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. đảm bảo employer tồn tại trong DB
        if (!employersJpaRepository.existsById(userId)) {
            Employers empCreate = new Employers();
            empCreate.setStatus("PENDING");
            empCreate.setUser(user);
            employersJpaRepository.saveAndFlush(empCreate);  // insert employer mới
        }

        // 3. lấy bản employer mới nhất từ DB
        Employers employer = employersJpaRepository.getReferenceById(userId);

        // 4. gán thông tin company pending
        CompanyInformation info = CompanyInformation.builder()
                .minEmployees(dto.getMinEmployee())
                .maxEmployees(dto.getMaxEmployee())
                .companyName(dto.getCompanyName())
                .logoUrl(dto.getLogo())
                .bannerUrl(dto.getBanner())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .industry(dto.getIndustry())
                .status("PENDING")
                .build();

        employer.setCompanyInformation(info);

        // 5. save employer (cascade insert CompanyInfo)
        employersJpaRepository.save(employer);

        // 6. cập nhật user
        user.setStatus("PENDING");
        userJpaRepository.save(user);
    }

    // admin duyệt
    public void approveUpgrade(Long userId) {
        Employers employer = employersJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // cập nhật trạng thái employer sang APPROVED
        employer.setStatus("APPROVED");

        // activate company
        employer.getCompanyInformation().setStatus("ACTIVE");
        Long idRole = 2L;

        //chuyển user sang role Employer
        roleService.changeUserRole(userId,idRole);

        // cập nhật user
        User user = employer.getUser();
        user.setStatus("EMPLOYER");

        employersJpaRepository.save(employer);
        userJpaRepository.save(user);
    }

    // admin từ chối
    @Transactional
    public void rejectUpgrade(Long userId) {
        Employers employer = employersJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Lấy user gốc để rollback
        User user = employer.getUser();
        user.setStatus("USER");  // rollback về user thường

        // Lưu user trước
        userJpaRepository.save(user);

        // Xoá company information (nếu có quan hệ cascade remove thì chỉ cần xóa employer)
        CompanyInformation companyInfo = employer.getCompanyInformation();
        if (companyInfo != null) {
            companyInformationRepository.delete(companyInfo);
        }

        // Xoá employer record
        employersJpaRepository.delete(employer);
    }

}