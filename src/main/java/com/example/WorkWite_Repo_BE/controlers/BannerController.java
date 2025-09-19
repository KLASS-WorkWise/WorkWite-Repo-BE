package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO;
import com.example.WorkWite_Repo_BE.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {
    @Value("${banner.upload.dir}")
    private String bannerUploadDir;
    
    // Lấy tất cả banner theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BannerResponseDTO>> getBannersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bannerService.getBannersByUserId(userId));
    }
    @GetMapping("/active")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBannersByPosition(@RequestParam String position) {
        return ResponseEntity.ok(bannerService.getActiveBannersByPosition(position));
    }

    private final BannerService bannerService;

    // Tạo banner (công ty gửi yêu cầu thuê, nhận từng trường qua form-data và file ảnh)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BannerResponseDTO> createBanner(
            @RequestParam String companyName,
            @RequestParam String companyEmail,
            @RequestParam String companyPhone,
            @RequestParam String companyWebsite,
            @RequestParam String bannerTitle,
            @RequestParam String bannerLink,
            @RequestParam String bannerType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String description,
            @RequestParam(value = "bannerImage", required = false) org.springframework.web.multipart.MultipartFile bannerImage
    ) {
        // Kiểm tra hợp lệ bannerType
        if (!"Vip".equalsIgnoreCase(bannerType) && !"Featured".equalsIgnoreCase(bannerType) && !"Standard".equalsIgnoreCase(bannerType)) {
            return ResponseEntity.badRequest().body(null);
        }
        String position;
        if ("Vip".equalsIgnoreCase(bannerType)) {
            position = "home_hero";
        } else if ("Featured".equalsIgnoreCase(bannerType)) {
            position = "featured";
        } else {
            position = "standard";
        }
        com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO dto = new com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO();
        dto.setCompanyName(companyName);
        dto.setCompanyEmail(companyEmail);
        dto.setCompanyPhone(companyPhone);
        dto.setCompanyWebsite(companyWebsite);
        dto.setBannerTitle(bannerTitle);
        dto.setBannerLink(bannerLink);
        dto.setBannerType(bannerType);
        dto.setPosition(position);
        dto.setStartDate(java.time.LocalDate.parse(startDate));
        dto.setEndDate(java.time.LocalDate.parse(endDate));
        dto.setDescription(description);
        System.out.println("bannerUploadDir = " + bannerUploadDir);
        System.out.println("bannerImage object = " + bannerImage);
        if (bannerImage != null && !bannerImage.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + org.springframework.util.StringUtils.cleanPath(bannerImage.getOriginalFilename());
            try {
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(bannerUploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                try (java.io.InputStream in = bannerImage.getInputStream()) {
                    java.nio.file.Files.copy(in, uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                // Trả về URL đầy đủ cho FE
                String baseUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                String imageUrl = baseUrl + "/uploads/banners/" + fileName;
                dto.setBannerImage(imageUrl);
                System.out.println("Saved bannerImage as: " + imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                dto.setBannerImage(null);
            }
        } else {
            System.out.println("No bannerImage sent or file is empty");
        }
        return ResponseEntity.ok(bannerService.createBanner(dto));
    }

    // Lấy danh sách tất cả banner
    @GetMapping
    public ResponseEntity<List<BannerResponseDTO>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    // Lấy banner theo id
    @GetMapping("/{id}")
    public ResponseEntity<BannerResponseDTO> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    // Cập nhật banner
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<BannerResponseDTO> updateBanner(
            @PathVariable Long id,
            @RequestParam String companyName,
            @RequestParam String companyEmail,
            @RequestParam String companyPhone,
            @RequestParam String companyWebsite,
            @RequestParam String bannerTitle,
            @RequestParam String bannerLink,
            @RequestParam String bannerType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String description,
            @RequestParam String position,
            @RequestParam(required = false) String bannerImageOld,
            @RequestParam(value = "bannerImage", required = false) org.springframework.web.multipart.MultipartFile bannerImage
    ) {
        com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO dto = new com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO();
        dto.setCompanyName(companyName);
        dto.setCompanyEmail(companyEmail);
        dto.setCompanyPhone(companyPhone);
        dto.setCompanyWebsite(companyWebsite);
        dto.setBannerTitle(bannerTitle);
        dto.setBannerLink(bannerLink);
        dto.setBannerType(bannerType);
        dto.setPosition(position);
        dto.setStartDate(java.time.LocalDate.parse(startDate));
        dto.setEndDate(java.time.LocalDate.parse(endDate));
        dto.setDescription(description);
        // Xử lý ảnh mới nếu có
        if (bannerImage != null && !bannerImage.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + org.springframework.util.StringUtils.cleanPath(bannerImage.getOriginalFilename());
            try {
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(bannerUploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                try (java.io.InputStream in = bannerImage.getInputStream()) {
                    java.nio.file.Files.copy(in, uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                String baseUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                String imageUrl = baseUrl + "/uploads/banners/" + fileName;
                dto.setBannerImage(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                dto.setBannerImage(bannerImageOld); // Nếu lỗi thì giữ ảnh cũ
            }
        } else {
            dto.setBannerImage(bannerImageOld); // Nếu không upload mới thì giữ ảnh cũ
        }
        return ResponseEntity.ok(bannerService.updateBanner(id, dto));
    }

    // Xóa banner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    // Admin duyệt banner
    @PatchMapping("/{id}/approve")
    public ResponseEntity<BannerResponseDTO> approveBanner(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.approveBanner(id));
    }

    // Admin từ chối banner
    @PatchMapping("/{id}/reject")
    public ResponseEntity<BannerResponseDTO> rejectBanner(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(bannerService.rejectBanner(id, reason));
    }
}
