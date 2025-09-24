package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO;
import java.util.List;

public interface BannerService {

    /**
     * Lấy danh sách banner đang active theo vị trí
     */
    List<BannerResponseDTO> getActiveBannersByPosition(String position);

    /**
     * Tạo banner mới
     */
    BannerResponseDTO createBanner(BannerRequestDTO requestDTO);

    /**
     * Lấy toàn bộ banner
     */
    List<BannerResponseDTO> getAllBanners();

    /**
     * Lấy banner theo ID
     */
    BannerResponseDTO getBannerById(Long id);

    /**
     * Xóa banner theo ID
     */
    void deleteBanner(Long id);

    /**
     * Cập nhật banner theo ID
     */
    BannerResponseDTO updateBanner(Long id, BannerRequestDTO requestDTO);

    /**
     * Duyệt banner
     */
    BannerResponseDTO approveBanner(Long id);

    /**
     * Từ chối banner với lý do
     */
    BannerResponseDTO rejectBanner(Long id, String reason);

    /**
     * Lấy danh sách banner theo userId
     */
    List<BannerResponseDTO> getBannersByUserId(Long userId);

    /**
     * Lấy danh sách banner theo status (dùng cho API active-list)
     */
    List<com.example.WorkWite_Repo_BE.entities.Banner> getBannersByStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus status);
}
