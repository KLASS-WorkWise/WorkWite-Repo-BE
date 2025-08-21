package com.example.WorkWite_Repo_BE.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name= "information_company")
public class CompanyInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String logoUrl;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String bannerUrl;
    @Column(unique = true)
    private String email;
    private String phone;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer minEmployees;
    private Integer maxEmployees;
    private String address;
    private String location;
    private String website;
    private String industry;
    private String status;

    @OneToOne(mappedBy = "companyInformation")
    @JsonIgnore
    private Employers employer;

}


