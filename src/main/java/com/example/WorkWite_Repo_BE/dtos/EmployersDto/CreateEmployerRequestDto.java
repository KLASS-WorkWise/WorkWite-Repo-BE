package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployerRequestDto {
    @NotBlank
    private String companyName;
    private String website;
    private String logo_url;
    private String address;
    private String email;
    private String industry;
    private String culture;
    private String benefits;
    private String media_url;

}