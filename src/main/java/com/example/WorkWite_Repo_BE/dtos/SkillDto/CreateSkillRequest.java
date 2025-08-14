package com.example.WorkWite_Repo_BE.dtos.SkillDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CreateSkillRequest {
    private String skillName;
   }
