package com.example.WorkWite_Repo_BE.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "awards")
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id", nullable = false)
    @JsonIgnore
    private Resume resume;


    @Column(name = "award_name")
    private String awardName;

    @Column(name = "don_vi_trao")
    private String donViTrao;

    @Column(name = "award_year")
    private Integer awardYear;

    private String description;
}
