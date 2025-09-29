
package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO;
import com.example.WorkWite_Repo_BE.entities.Banner;
import com.example.WorkWite_Repo_BE.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {
    // Lấy danh sách tất cả banner theo phân trang
    @GetMapping("/paginated")
    public ResponseEntity<com.example.WorkWite_Repo_BE.dtos.BannerDto.PaginatedBannerResponseDto> getAllBannersPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bannerService.getAllBannersPaginated(page, size));
    }
    @Value("${banner.upload.dir}")
    private String bannerUploadDir;
    
    // Lấy tất cả banner theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BannerResponseDTO>> getBannersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bannerService.getBannersByUserId(userId));
    }
    @GetMapping("/active")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBannersByType(@RequestParam String bannerType) {
        return ResponseEntity.ok(bannerService.getActiveBannersByType(bannerType));
    }

    private final BannerService bannerService;

    // Tạo banner (công ty gửi yêu cầu thuê, nhận từng trường qua form-data và file ảnh)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BannerResponseDTO> createBanner(
            @RequestParam String companyName,
            @RequestParam String companyEmail,
            @RequestParam String companyPhone,
            @RequestParam String bannerType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String description,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage
    ) {
        BannerResponseDTO response = bannerService.createBanner(companyName, companyEmail, companyPhone, bannerType, startDate, endDate, bannerImage, bannerUploadDir);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách tất cả banner
    @GetMapping
    public ResponseEntity<List<BannerResponseDTO>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    // Cập nhật banner
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<BannerResponseDTO> updateBanner(
            @PathVariable Long id,
            @RequestParam String companyName,
            @RequestParam String companyEmail,
            @RequestParam String companyPhone,
            @RequestParam String bannerType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String bannerImageOld,
        @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage
    ) {
        BannerResponseDTO response = bannerService.updateBanner(id, companyName, companyEmail, companyPhone, bannerType, startDate, endDate, bannerImageOld, bannerImage, bannerUploadDir);
        return ResponseEntity.ok(response);
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


        // API lấy tất cả banner active, trả về các trường cần thiết
    @GetMapping("/active-list")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBannerList() {
        List<BannerResponseDTO> response = bannerService.getActiveBannerList();
        return ResponseEntity.ok(response);
    }
}
