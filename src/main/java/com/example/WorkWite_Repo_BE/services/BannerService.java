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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final UserJpaRepository userJpaRepository;

    // ==========================
    // GET Methods
    // ==========================

    public PaginatedBannerResponseDto getAllBannersPaginated(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        var pageable = org.springframework.data.domain.PageRequest.of(pageNumber, size);
        var bannerPage = bannerRepository.findAll(pageable);

        List<BannerResponseDTO> bannerDtos = bannerPage.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        PaginatedBannerResponseDto dto = new PaginatedBannerResponseDto();
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
        return bannerRepository.findAll()
                .stream()
                .filter(b -> b.getUser() != null && b.getUser().getId().equals(userId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Banner> getBannersByStatus(BannerStatus status) {
        return bannerRepository.findByStatus(status);
    }

    public List<BannerResponseDTO> getActiveBannersByType(String bannerType) {
        return bannerRepository.findByBannerTypeAndStatus(bannerType, BannerStatus.ACTIVE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BannerResponseDTO> getAllBanners() {
        return bannerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // Business Logic
    // ==========================

    public void expireBannersIfNeeded() {
        List<Banner> banners = bannerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Banner banner : banners) {
            if (banner.getStatus() == BannerStatus.ACTIVE
                    && banner.getEndDate() != null
                    && now.isAfter(banner.getEndDate())) {
                banner.setStatus(BannerStatus.EXPIRED);
                banner.setUpdatedAt(now);
                bannerRepository.save(banner);
            }
        }
    }

    public BannerResponseDTO approveBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        banner.setStatus(BannerStatus.ACTIVE);
        banner.setUpdatedAt(LocalDateTime.now());

        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    public BannerResponseDTO rejectBanner(Long id, String reason) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        banner.setStatus(BannerStatus.REJECTED);
        banner.setDescription(reason != null ? reason : "");
        banner.setUpdatedAt(LocalDateTime.now());

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
        LocalDate start = requestDTO.getStartDate();
        LocalDate end = requestDTO.getEndDate();

        if (start == null || end == null || end.isBefore(start)) {
            throw new RuntimeException("Ngày bắt đầu/kết thúc không hợp lệ");
        }

        // Lấy user từ JWT
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        User user = username != null ? userJpaRepository.findByUsername(username).orElse(null) : null;
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Kiểm tra bannerType
        String type = requestDTO.getBannerType();
        if (type == null || !(type.equalsIgnoreCase("Vip")
                || type.equalsIgnoreCase("Featured")
                || type.equalsIgnoreCase("Standard"))) {
            throw new RuntimeException("Invalid bannerType. Must be Vip, Featured, or Standard");
        }

        // Business rule: Giới hạn mỗi công ty chỉ có tối đa 1 booking ACTIVE/PENDING cho 1 slot tại 1 thời điểm
        boolean exists = bannerRepository.findAll().stream()
            .anyMatch(b -> b.getBannerType() != null && b.getBannerType().equalsIgnoreCase(type)
                && b.getUser() != null && b.getUser().getId().equals(user.getId())
                && (b.getStatus() == BannerStatus.ACTIVE || b.getStatus() == BannerStatus.PENDING)
                && b.getEndDate() != null && !b.getEndDate().toLocalDate().isBefore(LocalDate.now())
            );
        if (exists) {
            throw new RuntimeException("Bạn đã có booking ở slot này. Vui lòng gia hạn hoặc chờ admin xử lý.");
        }

        // Xác định giá theo loại banner
        final long USD_TO_VND = 26410;
        long pricePerDay = switch (type.toLowerCase()) {
            case "vip" -> 3 * USD_TO_VND;
            case "featured" -> 2 * USD_TO_VND;
            default -> 1 * USD_TO_VND;
        };

        long days = ChronoUnit.DAYS.between(start, end) + 1;
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

        // Tạo banner mới
        Banner banner = new Banner();
        banner.setCompanyName(requestDTO.getCompanyName());
        banner.setCompanyEmail(requestDTO.getCompanyEmail());
        banner.setCompanyPhone(requestDTO.getCompanyPhone());
        banner.setBannerImage(requestDTO.getBannerImage());
        banner.setStartDate(start.atStartOfDay());
        banner.setEndDate(end.atStartOfDay());
        banner.setAmount(totalPrice);
        banner.setBannerType(type);
        banner.setDescription(requestDTO.getDescription());
        banner.setStatus(BannerStatus.PENDING);
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
        banner.setUser(user);

        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    // ==========================
    // CRUD
    // ==========================

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    public BannerResponseDTO updateBanner(Long id, BannerRequestDTO requestDTO) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        banner.setCompanyName(requestDTO.getCompanyName());
        banner.setCompanyEmail(requestDTO.getCompanyEmail());
        banner.setCompanyPhone(requestDTO.getCompanyPhone());
        banner.setBannerImage(requestDTO.getBannerImage());
        banner.setStartDate(requestDTO.getStartDate() != null ? requestDTO.getStartDate().atStartOfDay() : null);
        banner.setEndDate(requestDTO.getEndDate() != null ? requestDTO.getEndDate().atStartOfDay() : null);
        banner.setDescription(requestDTO.getDescription());
        banner.setBannerType(requestDTO.getBannerType());
        banner.setStatus(BannerStatus.PENDING);
        banner.setUpdatedAt(LocalDateTime.now());

        Banner saved = bannerRepository.save(banner);
        return toDTO(saved);
    }

    // ==========================
    // Helper
    // ==========================

    private BannerResponseDTO toDTO(Banner banner) {
        BannerResponseDTO dto = new BannerResponseDTO();
        dto.setId(banner.getId());
        dto.setBannerType(banner.getBannerType());
        dto.setCompanyName(banner.getCompanyName());
        dto.setCompanyEmail(banner.getCompanyEmail());
        dto.setCompanyPhone(banner.getCompanyPhone());
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
