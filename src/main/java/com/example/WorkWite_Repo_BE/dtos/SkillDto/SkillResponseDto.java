package com.example.WorkWite_Repo_BE.dtos.SkillDto;

import lombok.Data;

@Data
public class SkillResponseDto {

    private String skillName;

    public SkillResponseDto(String skillName) {

        this.skillName = skillName;
    }
}
