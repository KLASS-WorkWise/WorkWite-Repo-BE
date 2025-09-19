package com.example.WorkWite_Repo_BE.dtos.AboutUsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAboutUsRequestDto {
    private String companyName;
    private String companyTitle;
    private String companyDescription;
    // phần mô tả dịch vụ
    private String servicesSectionTitle;
    private String servicesSectionDescription;
    private String imageUrl;
}
