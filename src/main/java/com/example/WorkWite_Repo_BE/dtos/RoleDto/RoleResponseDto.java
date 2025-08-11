package com.example.WorkWite_Repo_BE.dtos.RoleDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto {
    private Long id;
    private String name;
    private String description;
}
