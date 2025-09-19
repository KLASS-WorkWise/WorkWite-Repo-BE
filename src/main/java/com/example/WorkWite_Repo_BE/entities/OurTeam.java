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
public class OurTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ourTeam;
    private String ourTeamTitle;
    @Lob
    private String ourTeamDescription;
    // phần card our team
    private String name;
    @Lob
    private String viTri;
    private String location;
    @Lob
    @Column(name = "image_url",columnDefinition = "LONGTEXT")
    private String imageUrl;
}
