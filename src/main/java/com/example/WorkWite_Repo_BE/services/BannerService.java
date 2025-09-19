
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO;
import java.util.List;

public interface BannerService {
    List<BannerResponseDTO> getActiveBannersByPosition(String position);
    BannerResponseDTO createBanner(BannerRequestDTO requestDTO);
    List<BannerResponseDTO> getAllBanners();
    BannerResponseDTO getBannerById(Long id);
    void deleteBanner(Long id);
    BannerResponseDTO updateBanner(Long id, BannerRequestDTO requestDTO);
    BannerResponseDTO approveBanner(Long id);
    BannerResponseDTO rejectBanner(Long id, String reason);

        List<com.example.WorkWite_Repo_BE.dtos.BannerDto.BannerResponseDTO> getBannersByUserId(Long userId);
}