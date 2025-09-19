package com.example.WorkWite_Repo_BE.dtos.BLogDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatBlogRequestDto {
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String imageUrl;
    private LocalDateTime createdAt;
//    private Long categoryId;
}

