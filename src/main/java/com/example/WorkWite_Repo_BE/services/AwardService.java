package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.AwardDto.AwardResponseDto;
import com.example.WorkWite_Repo_BE.dtos.AwardDto.CreatAwardRequestDto;
import com.example.WorkWite_Repo_BE.entities.Award;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.AwardJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AwardService {
    public AwardJpaRepository awardJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;

    public AwardService(AwardJpaRepository awardJpaRepository, ResumeJpaRepository resumeJpaRepository) {
        this.awardJpaRepository = awardJpaRepository;
        this.resumeJpaRepository = resumeJpaRepository;
    }

    // Convert từ Award entity sang AwardResponseDto
    private AwardResponseDto convertToDto(Award award) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new AwardResponseDto(
                award.getId(),
                award.getAwardName(),
                award.getAwardYear(),
                award.getDonViTrao(),
                award.getDescription());
    }

    public AwardResponseDto createAward(CreatAwardRequestDto createAwardDto, Long resumeId) {
        Award award = new Award();
        Resume resume = resumeJpaRepository.findById(resumeId).orElse(null);
        award.setAwardName(createAwardDto.getAwardName());
        award.setAwardYear(createAwardDto.getAwardYear());

        award.setResume(resume);
        Award awardNew = awardJpaRepository.save(award);
        return convertToDto(awardNew);
    }

    // Lấy tất cả awards theo resumeId
    public List<AwardResponseDto> getAllAwardsByResumeId(Long resumeId) {
        List<Award> awards = awardJpaRepository.findByResumeId(resumeId);
        return awards.stream().map(this::convertToDto).toList();
    }

    // Lấy award theo id
    public AwardResponseDto getAwardById(Long id) {
        Award award = awardJpaRepository.findById(id).orElse(null);
        if (award == null) return null;
        return convertToDto(award);
    }

    // Xóa award theo id
    public boolean deleteAward(Long id) {
        if (!awardJpaRepository.existsById(id)) return false;
        awardJpaRepository.deleteById(id);
        return true;
    }

}
