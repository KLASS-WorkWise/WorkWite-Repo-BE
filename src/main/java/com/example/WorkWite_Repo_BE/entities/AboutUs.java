package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class AboutUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String companyTitle;
    @Lob
    private String companyDescription;
    // phần mô tả dịch vụ
    private String servicesSectionTitle;
    @Lob
    private String servicesSectionDescription;
    @Lob
    @Column(name = "image_url",columnDefinition = "LONGTEXT")
    private String imageUrl;
}
