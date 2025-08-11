package com.example.WorkWite_Repo_BE.entities;

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
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "award_name")
    private String awardName;

    @Column(name = "award_year")
    private Integer awardYear;
}
