package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class EmployerResponseDto {
    private String companyName;
    private String website;
    private String logo_url;
    private String address;
    private String email;
    private String industry;
    private String culture;
    private String benefits;
    private String media_url;

    public EmployerResponseDto(String companyName, String website, String logo_url, String address, String email, String industry, String culture, String benefits, String media_url) {
        this.companyName = companyName;
        this.website = website;
        this.logo_url = logo_url;
        this.address = address;
        this.email = email;
        this.industry = industry;
        this.culture = culture;
        this.benefits = benefits;
        this.media_url = media_url;
    }

}

