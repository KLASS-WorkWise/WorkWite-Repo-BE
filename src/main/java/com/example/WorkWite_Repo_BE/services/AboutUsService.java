package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.AboutUsResponseDto;
import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.CreateAboutUsRequestDto;
import com.example.WorkWite_Repo_BE.dtos.AboutUsDto.UpdateAboutUsRequestDto;
import com.example.WorkWite_Repo_BE.entities.AboutUs;
import com.example.WorkWite_Repo_BE.repositories.AboutUsJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AboutUsService {
    private final AboutUsJpaRepository aboutUsJpaRepositoty;

    public AboutUsService(AboutUsJpaRepository aboutUsJpaRepositoty) {
        this.aboutUsJpaRepositoty = aboutUsJpaRepositoty;
    }

    //convert entity to dto
    public AboutUsResponseDto convertDto(AboutUs aboutUs){
        return new AboutUsResponseDto(
                aboutUs.getId(),
                aboutUs.getCompanyName(),
                aboutUs.getCompanyTitle(),
                aboutUs.getCompanyDescription(),
                aboutUs.getServicesSectionTitle(),
                aboutUs.getServicesSectionDescription(),
                aboutUs.getImageUrl()
        );
    }
    //creat AboutUs
    public AboutUsResponseDto createAboutUs(CreateAboutUsRequestDto creatAboutUs) {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setCompanyName(creatAboutUs.getCompanyName());
        aboutUs.setCompanyTitle(creatAboutUs.getCompanyTitle());
        aboutUs.setCompanyDescription(creatAboutUs.getCompanyDescription());
        aboutUs.setServicesSectionTitle(creatAboutUs.getServicesSectionTitle());
        aboutUs.setServicesSectionDescription(creatAboutUs.getServicesSectionDescription());
        aboutUs.setImageUrl(creatAboutUs.getImageUrl());

        AboutUs aboutUsAdd = aboutUsJpaRepositoty.save(aboutUs);
        return convertDto(aboutUsAdd);
    }
    // get add aboutUs
    public List<AboutUsResponseDto> getAllAboutUs(){
        List<AboutUs> aboutUs = aboutUsJpaRepositoty.findAll();
        return aboutUs.stream().map(this::convertDto).toList();

    }
    // get by id
    public AboutUsResponseDto getAboutUsById(Long id) {
        AboutUs aboutUs = aboutUsJpaRepositoty.findById(id).orElse(null);
        if (aboutUs == null) return null;
        return convertDto(aboutUs);
    }
    // delete by id
    public boolean deleteAboutUsById(Long id) {
        if (!aboutUsJpaRepositoty.existsById(id)) {
            return false;
        }
        aboutUsJpaRepositoty.deleteById(id);
        return true;
    }
    //update aboutUs using UpdateAboutUsRequestDto
    public AboutUsResponseDto updateAboutUs(Long id, UpdateAboutUsRequestDto updateAboutUs) {
        AboutUs aboutUs = aboutUsJpaRepositoty.findById(id).orElse(null);
        if (aboutUs == null) return null;
        aboutUs.setCompanyName(updateAboutUs.getCompanyName());
        aboutUs.setCompanyTitle(updateAboutUs.getCompanyTitle());
        aboutUs.setCompanyDescription(updateAboutUs.getCompanyDescription());
        aboutUs.setServicesSectionTitle(updateAboutUs.getServicesSectionTitle());
        aboutUs.setServicesSectionDescription(updateAboutUs.getServicesSectionDescription());
        aboutUs.setImageUrl(updateAboutUs.getImageUrl());

        AboutUs aboutUsUpdate = aboutUsJpaRepositoty.save(aboutUs);
        return convertDto(aboutUsUpdate);
    }


}
