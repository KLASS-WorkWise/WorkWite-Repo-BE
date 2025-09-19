package com.example.WorkWite_Repo_BE.dtos.BLogDto;

import com.example.WorkWite_Repo_BE.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String imageUrl;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
