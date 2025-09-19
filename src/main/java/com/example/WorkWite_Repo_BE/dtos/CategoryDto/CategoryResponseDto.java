package com.example.WorkWite_Repo_BE.dtos.CategoryDto;
import com.example.WorkWite_Repo_BE.entities.BLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<BLog> blog;
}
