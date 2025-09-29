package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.BLogDto.BlogResponseDto;
import com.example.WorkWite_Repo_BE.dtos.BLogDto.CreatBlogRequestDto;
import com.example.WorkWite_Repo_BE.dtos.BLogDto.UpdateBlogRequestDto;
import com.example.WorkWite_Repo_BE.entities.BLog;
import com.example.WorkWite_Repo_BE.entities.Category;
import com.example.WorkWite_Repo_BE.repositories.BlogJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.CategoryJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BlogService {
    private final BlogJpaRepository blogJpaRepository;
    private final CategoryJpaRepository categoryJpaRepositpry;

    public BlogService(BlogJpaRepository blogJpaRepository, CategoryJpaRepository categoryJpaRepositpry) {
        this.blogJpaRepository = blogJpaRepository;
        this.categoryJpaRepositpry = categoryJpaRepositpry;
    }

    //convert từ Blog entity sang BlogResponseDto
    private BlogResponseDto convertToDto(BLog blog){
        return new BlogResponseDto(
                blog.getId(),
                blog.getTitle(),
                blog.getSlug(),
                blog.getContent(),
                blog.getSummary(),
                blog.getImageUrl(),
                blog.getCategory(),
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }
    // get all
    public List<BlogResponseDto> getAllBlog(){
        List<BLog> blogs = blogJpaRepository.findAll();
        return blogs.stream().map(this::convertToDto).toList();
    }
    // get by id
    public BlogResponseDto getBlogById(Long id){
        BLog blog = blogJpaRepository.findById(id).orElse(null);
        if(blog == null) return null;
        return convertToDto(blog);
    }
    //creat blog
    public BlogResponseDto creatBlog(CreatBlogRequestDto creatBlog, Long categoryId){
       Category category = categoryJpaRepositpry.findById(categoryId).orElse(null);
        BLog blog = new BLog();
        blog.setTitle(creatBlog.getTitle());
        blog.setSlug(creatBlog.getSlug());
        blog.setContent(creatBlog.getContent());
        blog.setSummary(creatBlog.getSummary());
        blog.setImageUrl(creatBlog.getImageUrl());
        blog.setCreatedAt(java.time.LocalDateTime.now());
        blog.setCategory(category);
        BLog blogNew = blogJpaRepository.save(blog);
        return convertToDto(blogNew);
    }

    // delete blog by id
    public boolean deleteBlogById(Long id) {
        BLog blog = blogJpaRepository.findById(id).orElse(null);
        if (blog == null) return false;
        blogJpaRepository.deleteById(id);
        return true;
    }

    // update blog by id
    public BlogResponseDto updateBlog(Long id, UpdateBlogRequestDto updateDto, Long categoryId) {
        BLog blog = blogJpaRepository.findById(id).orElse(null);
        if (blog == null) return null;
        blog.setTitle(updateDto.getTitle());
        blog.setSlug(updateDto.getSlug());
        blog.setContent(updateDto.getContent());
        blog.setSummary(updateDto.getSummary());
        blog.setImageUrl(updateDto.getImageUrl());
        if (categoryId != null) {
            Category category = categoryJpaRepositpry.findById(categoryId).orElse(null);
            blog.setCategory(category);
        }
        blog.setUpdatedAt(java.time.LocalDateTime.now());
        BLog updatedBlog = blogJpaRepository.save(blog);
        return convertToDto(updatedBlog);
    }

    // get by slug
    public BlogResponseDto getBlogBySlug(String slug) {
        BLog blog = blogJpaRepository.findBySlug(slug).orElse(null);
        if (blog == null) return null;
        return convertToDto(blog);
    }

}
