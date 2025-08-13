package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "information_comany")
public class CompanyInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer employee;
    private String companyName;
    private String logoUrl;
    private String bannerUrl;
    private String email;
    private String phone;
    private String description;
    private LocalDateTime lastPosted;
    private String address;
    private String location;
    private String website;
    private String industry;

    @OneToOne(mappedBy = "companyInformation")
    private Employers employer;
}
