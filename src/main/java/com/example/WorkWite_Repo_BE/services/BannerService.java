
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO;
import com.example.WorkWite_Repo_BE.dtos.BannerDto.PaginatedBannerResponseDto;
import com.example.WorkWite_Repo_BE.entities.Banner;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.enums.BannerStatus;
import com.example.WorkWite_Repo_BE.repositories.BannerRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final UserJpaRepository userJpaRepository;
    
    public PaginatedBannerResponseDto getAllBannersPaginated(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, size);
        org.springframework.data.domain.Page<Banner> bannerPage = bannerRepository.findAll(pageable);
        List<BannerResponseDTO> bannerDtos = bannerPage.getContent().stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
        com.example.WorkWite_Repo_BE.dtos.BannerDto.PaginatedBannerResponseDto dto = new com.example.WorkWite_Repo_BE.dtos.BannerDto.PaginatedBannerResponseDto();
        dto.setData(bannerDtos);
        dto.setPageNumber(bannerPage.getNumber() + 1);
        dto.setPageSize(bannerPage.getSize());
        dto.setTotalRecords(bannerPage.getTotalElements());
        dto.setTotalPages(bannerPage.getTotalPages());
        dto.setHasNext(bannerPage.hasNext());
        dto.setHasPrevious(bannerPage.hasPrevious());
        return dto;
    }

    public List<BannerResponseDTO> getBannersByUserId(Long userId) {
        return bannerRepository.findAll().stream()
            .filter(b -> b.getUser() != null && b.getUser().getId().equals(userId))
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    // Lấy danh sách banner theo status (dùng cho API active-list)
    public List<Banner> getBannersByStatus(BannerStatus status) {
        return bannerRepository.findByStatus(status);
    }

    // Xử lý hết hạn banner
    public void expireBannersIfNeeded() {
        List<Banner> banners = bannerRepository.findAll();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (Banner banner : banners) {
            if (banner.getStatus() == com.example.WorkWite_Repo_BE.enums.BannerStatus.ACTIVE && banner.getEndDate() != null && now.isAfter(banner.getEndDate())) {
                banner.setStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.EXPIRED);
                banner.setUpdatedAt(now);
                bannerRepository.save(banner);
            }
        }
    }
    public List<BannerResponseDTO> getActiveBannersByType(String bannerType) {
        return bannerRepository.findByBannerTypeAndStatus(bannerType, BannerStatus.ACTIVE)
            .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }
    public BannerResponseDTO approveBanner(Long id) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.ACTIVE);
        banner.setUpdatedAt(java.time.LocalDateTime.now());
        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    public BannerResponseDTO rejectBanner(Long id, String reason) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.REJECTED);
        banner.setDescription((reason != null ? reason : ""));
        banner.setUpdatedAt(java.time.LocalDateTime.now());
        // Hoàn tiền cho user nếu banner bị từ chối
        User user = banner.getUser();
        if (user != null && banner.getAmount() != null) {
            user.setBalance(user.getBalance() + banner.getAmount());
            userJpaRepository.save(user);
        }
        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    public BannerResponseDTO createBanner(BannerRequestDTO requestDTO) {
        // Lấy user từ JWT (SecurityContextHolder)
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        User user = username != null ? userJpaRepository.findByUsername(username).orElse(null) : null;
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Lấy bannerType từ request, kiểm tra hợp lệ
        String type = requestDTO.getBannerType();
        if (type == null ||
            !(type.equalsIgnoreCase("Vip") || type.equalsIgnoreCase("Featured") || type.equalsIgnoreCase("Standard"))) {
            throw new RuntimeException("Invalid bannerType. Must be Vip, Featured, or Standard");
        }
        final long USD_TO_VND = 26410;
        long pricePerDay;
        if ("Vip".equalsIgnoreCase(type)) {
            pricePerDay = 3 * USD_TO_VND;
        } else if ("Featured".equalsIgnoreCase(type)) {
            pricePerDay = 2 * USD_TO_VND;
        } else {
            pricePerDay = 1 * USD_TO_VND;
        }

        // Tính số ngày thuê (bao gồm cả ngày bắt đầu và kết thúc)
        java.time.LocalDate start = requestDTO.getStartDate();
        java.time.LocalDate end = requestDTO.getEndDate();
        if (start == null || end == null || end.isBefore(start)) {
            throw new RuntimeException("Ngày bắt đầu/kết thúc không hợp lệ");
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        if (days <= 0) {
            throw new RuntimeException("Số ngày thuê phải lớn hơn 0");
        }

        long totalPrice = pricePerDay * days;

        // Kiểm tra số dư
        if (user.getBalance() == null || user.getBalance() < totalPrice) {
            throw new RuntimeException("Số dư không đủ để thuê banner");
        }

        // Trừ tiền
        user.setBalance(user.getBalance() - totalPrice);
        userJpaRepository.save(user);

        Banner banner = new Banner();
        banner.setCompanyName(requestDTO.getCompanyName());
        banner.setCompanyEmail(requestDTO.getCompanyEmail());
        banner.setCompanyPhone(requestDTO.getCompanyPhone());
        banner.setCompanyWebsite(requestDTO.getCompanyWebsite());
        banner.setBannerTitle(requestDTO.getBannerTitle());
        banner.setBannerImage(requestDTO.getBannerImage());
        banner.setStartDate(start != null ? start.atStartOfDay() : null);
        banner.setEndDate(end != null ? end.atStartOfDay() : null);
        banner.setAmount(totalPrice); // Số tiền đã trừ
        banner.setBannerType(type);
        banner.setDescription(requestDTO.getDescription());
        banner.setStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.PENDING);
        banner.setCreatedAt(java.time.LocalDateTime.now());
        banner.setUpdatedAt(java.time.LocalDateTime.now());
        banner.setUser(user);
        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    public List<BannerResponseDTO> getAllBanners() {
        return bannerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }


    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    public BannerResponseDTO updateBanner(Long id, BannerRequestDTO requestDTO) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setCompanyName(requestDTO.getCompanyName());
        banner.setCompanyEmail(requestDTO.getCompanyEmail());
        banner.setCompanyPhone(requestDTO.getCompanyPhone());
        banner.setCompanyWebsite(requestDTO.getCompanyWebsite());
        banner.setBannerTitle(requestDTO.getBannerTitle());
        banner.setBannerImage(requestDTO.getBannerImage());
        banner.setStartDate(requestDTO.getStartDate() != null ? requestDTO.getStartDate().atStartOfDay() : null);
        banner.setEndDate(requestDTO.getEndDate() != null ? requestDTO.getEndDate().atStartOfDay() : null);
    // Không cho phép cập nhật amount từ request, giữ nguyên amount cũ
        banner.setDescription(requestDTO.getDescription());
        banner.setBannerType(requestDTO.getBannerType());
        banner.setStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.PENDING); // Đặt lại trạng thái về PENDING
        banner.setUpdatedAt(java.time.LocalDateTime.now());
        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    private BannerResponseDTO toDTO(Banner banner) {
        BannerResponseDTO dto = new BannerResponseDTO();
        dto.setBannerType(banner.getBannerType());
        dto.setId(banner.getId());
        dto.setCompanyName(banner.getCompanyName());
        dto.setCompanyEmail(banner.getCompanyEmail());
        dto.setCompanyPhone(banner.getCompanyPhone());
        dto.setCompanyWebsite(banner.getCompanyWebsite());
        dto.setBannerTitle(banner.getBannerTitle());
        dto.setBannerImage(banner.getBannerImage());
        dto.setStartDate(banner.getStartDate());
        dto.setEndDate(banner.getEndDate());
        dto.setAmount(banner.getAmount());
        dto.setDescription(banner.getDescription());
        dto.setStatus(banner.getStatus() != null ? banner.getStatus().name() : null);
        dto.setCreatedAt(banner.getCreatedAt());
        dto.setUpdatedAt(banner.getUpdatedAt());
        if (banner.getUser() != null) {
            dto.setUserId(banner.getUser().getId());
            dto.setUserName(banner.getUser().getFullName());
        }
        return dto;
    }
}
