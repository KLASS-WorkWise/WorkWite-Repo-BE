package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.*;
import com.example.WorkWite_Repo_BE.services.EmployersService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/employers")
public class EmployersControler {
    private final EmployersService employersService;

    public EmployersControler(EmployersService employersService) {
        this.employersService = employersService;
    }

    @GetMapping("")
    public PaginatedEmployerRespondeDto getAllEmployers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return this.employersService.getPaginatedEmployers(page, size);
    }
    @GetMapping("/upgradeEmployer")
    public PaginatedEmployerRespondeDto getUpgradeEmployer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return this.employersService.getPaginatedUpgradeEmployers(page, size);
    }

    @GetMapping("/{id}")
    public EmployerResponseDto getEmployerById(@PathVariable Long id) {
        return employersService.getEmployerById(id);
    }

    @PatchMapping("/profile/{id}")
    public EmployerResponseDto updateEmployerProfile(@PathVariable Long id,
            @RequestBody UpdateEmployerRequestDto employerResponseDto) {
        return employersService.updateEmployerProfile(id, employerResponseDto);
    }

    // người dùng gửi yêu cầu nâng cấp
    // sử dụng formData

    @PostMapping(
            value = "/{userId}/upgrade-to-employer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> sendUpgradeRequest(
            @PathVariable Long userId,
            @RequestParam("companyName") String companyName,
            @RequestParam("minEmployee") Integer minEmployee,
            @RequestParam(value = "maxEmployee", required = false) Integer maxEmployee,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("industry") String industry,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "website", required = false) String website,
            @RequestPart(value = "logo", required = false) String logo,
            @RequestPart(value = "banner", required = false) String banner
    ) {
        // Chuyển dữ liệu vào DTO hoặc trực tiếp gọi service
        UpgradeEmployerRequestDto dto = new UpgradeEmployerRequestDto();
        dto.setCompanyName(companyName);
        dto.setMinEmployee(minEmployee);
        dto.setMaxEmployee(maxEmployee);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setIndustry(industry);
        dto.setDescription(description);
        dto.setAddress(address);
        dto.setLocation(location);
        dto.setWebsite(website);
        dto.setLogo(logo);
        dto.setBanner(banner);

        employersService.sendUpgradeRequest(userId, dto);

        return ResponseEntity.ok("Đã gửi yêu cầu, vui lòng chờ duyệt!");
    }



}
