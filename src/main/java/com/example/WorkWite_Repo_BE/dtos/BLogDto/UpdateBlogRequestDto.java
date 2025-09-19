package com.example.WorkWite_Repo_BE.dtos.BLogDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBlogRequestDto {
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String imageUrl;
    private Long categoryId;
}
