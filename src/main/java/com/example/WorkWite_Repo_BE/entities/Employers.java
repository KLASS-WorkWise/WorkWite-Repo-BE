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
@DiscriminatorValue("EMPLOYER")
@PrimaryKeyJoinColumn(name = "id")
public class Employers extends User {

    private Boolean status;
    private String phoneNumber;
    private String avatar;
    private String fullName;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_information_id", referencedColumnName = "id")
    private CompanyInformation companyInformation;

}