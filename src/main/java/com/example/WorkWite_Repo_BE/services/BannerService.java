
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
    public List<BannerResponseDTO> getActiveBannersByPosition(String position) {
        return bannerRepository.findByPositionAndStatus(position, com.example.WorkWite_Repo_BE.enums.BannerStatus.ACTIVE)
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
        // Lấy user từ SecurityContextHolder (JWT)
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        User user = username != null ? userJpaRepository.findByUsername(username).orElse(null) : null;
        // Tự động gán position và giá tiền theo bannerType
        long price;
        String type = requestDTO.getBannerType() != null ? requestDTO.getBannerType() : "Vip";
        String position;
        if ("Vip".equalsIgnoreCase(type)) {
            price = 5;
            position = "home_hero";
        } else if ("Featured".equalsIgnoreCase(type)) {
            price = 2;
            position = "sidebar_right";
        } else if ("Standard".equalsIgnoreCase(type)) {
            price = 1;
            position = "footer";
        } else {
            throw new RuntimeException("Invalid bannerType. Must be Vip, Featured, or Standard");
        }

        // Kiểm tra số dư
        if (user == null || user.getBalance() == null || user.getBalance() < price) {
            throw new com.example.WorkWite_Repo_BE.exceptions.InsufficientBalanceException(
                user != null ? user.getId() : null,
                user != null ? user.getBalance() : null,
                price,
                type
            );
        }
        user.setBalance(user.getBalance() - price);
        userJpaRepository.save(user);

        Banner banner = new Banner();
        banner.setCompanyName(requestDTO.getCompanyName());
        banner.setCompanyEmail(requestDTO.getCompanyEmail());
        banner.setCompanyPhone(requestDTO.getCompanyPhone());
        banner.setCompanyWebsite(requestDTO.getCompanyWebsite());
        banner.setBannerTitle(requestDTO.getBannerTitle());
        banner.setBannerImage(requestDTO.getBannerImage());
        banner.setBannerLink(requestDTO.getBannerLink());
        banner.setPosition(position);
        banner.setStartDate(requestDTO.getStartDate() != null ? requestDTO.getStartDate().atStartOfDay() : null);
        banner.setEndDate(requestDTO.getEndDate() != null ? requestDTO.getEndDate().atStartOfDay() : null);
        banner.setAmount(price);
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

    public BannerResponseDTO getBannerById(Long id) {
        return bannerRepository.findById(id).map(this::toDTO).orElseThrow(() -> new RuntimeException("Banner not found"));
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
        banner.setBannerLink(requestDTO.getBannerLink());
        banner.setPosition(requestDTO.getPosition());
        banner.setStartDate(requestDTO.getStartDate() != null ? requestDTO.getStartDate().atStartOfDay() : null);
        banner.setEndDate(requestDTO.getEndDate() != null ? requestDTO.getEndDate().atStartOfDay() : null);
        banner.setAmount(requestDTO.getAmount());
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
        dto.setBannerLink(banner.getBannerLink());
        dto.setPosition(banner.getPosition());
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
