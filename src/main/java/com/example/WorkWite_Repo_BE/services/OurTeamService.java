package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.CreateOurTeamRequestDto;
import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.OurTeamResponseDto;
import com.example.WorkWite_Repo_BE.dtos.OurTeamDto.UpdateOurTeamRequestDto;
import com.example.WorkWite_Repo_BE.entities.OurTeam;
import com.example.WorkWite_Repo_BE.repositories.OurTeamJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OurTeamService {
    private final OurTeamJpaRepository ourTeamJpaRepository;

    public OurTeamService(OurTeamJpaRepository ourTeamJpaRepository) {
        this.ourTeamJpaRepository = ourTeamJpaRepository;
    }

    //convert entity to dto
    public OurTeamResponseDto convertDto(OurTeam ourTeam){
        return OurTeamResponseDto.builder()
                .id(ourTeam.getId())
                .ourTeam(ourTeam.getOurTeam())
                .ourTeamTitle(ourTeam.getOurTeamTitle())
                .ourTeamDescription(ourTeam.getOurTeamDescription())
                .name(ourTeam.getName())
                .viTri(ourTeam.getViTri())
                .location(ourTeam.getLocation())
                .imageUrl(ourTeam.getImageUrl())
                .build();
    }

    // creat
    public OurTeamResponseDto creatOurTeam(CreateOurTeamRequestDto createOurTeamRequestDto){
       OurTeam ourTeam = new OurTeam();
            ourTeam.setOurTeam(createOurTeamRequestDto.getOurTeam());
            ourTeam.setOurTeamTitle(createOurTeamRequestDto.getOurTeamTitle());
            ourTeam.setOurTeamDescription(createOurTeamRequestDto.getOurTeamDescription());
            ourTeam.setName(createOurTeamRequestDto.getName());
            ourTeam.setViTri(createOurTeamRequestDto.getViTri());
            ourTeam.setLocation(createOurTeamRequestDto.getLocation());
            ourTeam.setImageUrl(createOurTeamRequestDto.getImageUrl());
            OurTeam ourTeamAdd = ourTeamJpaRepository.save(ourTeam);
            return convertDto(ourTeamAdd);
    }

    // get all
    public List<OurTeamResponseDto> getAllOurTeam(){
        List<OurTeam> ourTeam = ourTeamJpaRepository.findAll();
        return ourTeam.stream().map(this::convertDto).toList();
    }
    // get by id
    public OurTeamResponseDto getOurTeamById(Long id) {
        OurTeam ourTeam = ourTeamJpaRepository.findById(id).orElse(null);
        if (ourTeam == null) return null;
        return convertDto(ourTeam);
    }
    // delete by id
    public boolean deleteOurTeamById(Long id) {
        if (!ourTeamJpaRepository.existsById(id)) {
            return false;
        }
        ourTeamJpaRepository.deleteById(id);
        return true;
    }
    // update by id
    public OurTeamResponseDto updateOurTeam(Long id, UpdateOurTeamRequestDto updateOurTeam){
        OurTeam ourTeam = ourTeamJpaRepository.findById(id).orElse(null);
        if (ourTeam == null) return null;
        ourTeam.setOurTeam(updateOurTeam.getOurTeam());
        ourTeam.setOurTeamTitle(updateOurTeam.getOurTeamTitle());
        ourTeam.setOurTeamDescription(updateOurTeam.getOurTeamDescription());
        ourTeam.setName(updateOurTeam.getName());
        ourTeam.setViTri(updateOurTeam.getViTri());
        ourTeam.setLocation(updateOurTeam.getLocation());
        ourTeam.setImageUrl(updateOurTeam.getImageUrl());
        OurTeam ourTeamUpdate = ourTeamJpaRepository.save(ourTeam);
        return convertDto(ourTeamUpdate);
    }

}