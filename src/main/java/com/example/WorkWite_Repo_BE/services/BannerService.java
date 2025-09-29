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
    // Trả về danh sách BannerResponseDTO cho các banner ACTIVE, chỉ set các trường cần thiết
    public List<BannerResponseDTO> getActiveBannerList() {
        List<Banner> activeBanners = getBannersByStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus.ACTIVE);
        return activeBanners.stream()
            .map(b -> {
                BannerResponseDTO dto = new BannerResponseDTO();
                dto.setCompanyName(b.getCompanyName());
                dto.setStartDate(b.getStartDate());
                dto.setEndDate(b.getEndDate());
                dto.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
                dto.setBannerImage(b.getBannerImage());
                // Có thể set thêm các trường khác nếu cần
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    // New method: handles all business logic and file upload for banner creation
    public BannerResponseDTO createBanner(String companyName, String companyEmail, String companyPhone, String bannerType, String startDate, String endDate,  org.springframework.web.multipart.MultipartFile bannerImage, String bannerUploadDir) {
        BannerRequestDTO dto = new BannerRequestDTO();
        dto.setCompanyName(companyName);
        dto.setCompanyEmail(companyEmail);
        dto.setCompanyPhone(companyPhone);
        dto.setBannerType(bannerType);
        dto.setStartDate(java.time.LocalDate.parse(startDate));
        dto.setEndDate(java.time.LocalDate.parse(endDate));
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
                dto.setBannerImage(null);
            }
        }
        return createBanner(dto);
    }

    // New method: handles all business logic and file upload for banner update
    public BannerResponseDTO updateBanner(Long id, String companyName, String companyEmail, String companyPhone, String bannerType, String startDate, String endDate,  String bannerImageOld, org.springframework.web.multipart.MultipartFile bannerImage, String bannerUploadDir) {
        BannerRequestDTO dto = new BannerRequestDTO();
        dto.setCompanyName(companyName);
        dto.setCompanyEmail(companyEmail);
        dto.setCompanyPhone(companyPhone);
        dto.setBannerType(bannerType);
        dto.setStartDate(java.time.LocalDate.parse(startDate));
        dto.setEndDate(java.time.LocalDate.parse(endDate));
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
        return updateBanner(id, dto);
    }
    /**
     * Kiểm tra và kích hoạt các banner có startDate <= hiện tại và đang ở trạng thái PENDING
     */
    public void activateBannersIfNeeded() {
        List<Banner> banners = bannerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Banner banner : banners) {
            if (banner.getStatus() == BannerStatus.PENDING
                    && banner.getStartDate() != null
                    && !now.isBefore(banner.getStartDate())) {
                banner.setStatus(BannerStatus.ACTIVE);
                banner.setUpdatedAt(now);
                bannerRepository.save(banner);
            }
        }
    }

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
    LocalDateTime now = LocalDateTime.now();
        if (banner.getStartDate() != null && now.isBefore(banner.getStartDate())) {
            // Nếu ngày bắt đầu là tương lai, set trạng thái APPROVED (hoặc WAITING)
            banner.setStatus(BannerStatus.APPROVED); // Nếu chưa có enum APPROVED thì thêm vào enums/BannerStatus.java
        } else {
            // Nếu ngày bắt đầu <= hiện tại, set ACTIVE
            banner.setStatus(BannerStatus.ACTIVE);
        }
    banner.setUpdatedAt(now);
    Banner saved = bannerRepository.save(banner);
    return toDTO(saved);
    }

    public BannerResponseDTO rejectBanner(Long id, String reason) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        banner.setStatus(BannerStatus.REJECTED);
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

        // Business rule: Giới hạn mỗi công ty chỉ có tối đa 1 booking ACTIVE/PENDING cho 1 slot tại 1 thời điểm (kiểm tra giao nhau thời gian)
        boolean exists = bannerRepository.findAll().stream()
            .anyMatch(b -> b.getBannerType() != null && b.getBannerType().equalsIgnoreCase(type)
                && b.getUser() != null && b.getUser().getId().equals(user.getId())
                && (b.getStatus() == BannerStatus.ACTIVE || b.getStatus() == BannerStatus.PENDING)
                && b.getStartDate() != null && b.getEndDate() != null
                && !(end.atStartOfDay().isBefore(b.getStartDate()) || start.atStartOfDay().isAfter(b.getEndDate()))
            );
        if (exists) {
            throw new RuntimeException("Bạn đã có booking ở slot này (thời gian bị giao nhau). Vui lòng gia hạn hoặc chờ admin xử lý.");
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
    // Luôn set trạng thái PENDING khi tạo mới, không tự động duyệt
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
