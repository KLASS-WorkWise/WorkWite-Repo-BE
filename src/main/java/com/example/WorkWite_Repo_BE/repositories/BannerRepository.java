package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
	java.util.List<Banner> findByBannerTypeAndStatus(String bannerType, com.example.WorkWite_Repo_BE.enums.BannerStatus status);

	java.util.List<Banner> findByStatus(com.example.WorkWite_Repo_BE.enums.BannerStatus status);
}