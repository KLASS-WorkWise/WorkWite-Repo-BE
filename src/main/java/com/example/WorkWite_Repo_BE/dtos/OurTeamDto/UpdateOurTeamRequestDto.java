package com.example.WorkWite_Repo_BE.dtos.OurTeamDto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOurTeamRequestDto {
    private String ourTeam;
    private String ourTeamTitle;
    private String ourTeamDescription;
    // phần card our team
    private String name;
    private String viTri;
    private String location;
    private String imageUrl;
}
