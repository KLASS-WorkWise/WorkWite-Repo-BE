package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
	java.util.List<Banner> findByPositionAndStatus(String position, com.example.WorkWite_Repo_BE.enums.BannerStatus status);
}
