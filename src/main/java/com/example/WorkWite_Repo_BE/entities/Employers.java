package com.example.WorkWite_Repo_BE.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Employers {

    @Id
    private Long id; // Sẽ dùng chung với id của User

    private String phoneNumber;
    private String avatar;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @MapsId // Lấy id từ User
    @JoinColumn(name = "id") // cột id vừa là PK vừa là FK
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_information_id", referencedColumnName = "id")
    private CompanyInformation companyInformation;
}