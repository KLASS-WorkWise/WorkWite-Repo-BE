package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.BLogDto.BlogResponseDto;
import com.example.WorkWite_Repo_BE.dtos.BLogDto.CreatBlogRequestDto;
import com.example.WorkWite_Repo_BE.dtos.BLogDto.UpdateBlogRequestDto;
import com.example.WorkWite_Repo_BE.services.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<List<BlogResponseDto>> getAllBlogs() {
        List<BlogResponseDto> blogs = blogService.getAllBlog();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponseDto> getBlogById(@PathVariable Long id) {
        BlogResponseDto blog = blogService.getBlogById(id);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<BlogResponseDto> createBlog(@PathVariable Long categoryId ,@RequestBody CreatBlogRequestDto requestDto) {
        BlogResponseDto blog = blogService.creatBlog(requestDto, categoryId);
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponseDto> updateBlog(
            @PathVariable Long id,
            @RequestBody UpdateBlogRequestDto updateDto) {
        BlogResponseDto updatedBlog = blogService.updateBlog(id, updateDto, updateDto.getCategoryId());
        if (updatedBlog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        boolean deleted = blogService.deleteBlogById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogResponseDto> getBlogBySlug(@PathVariable String slug) {
        BlogResponseDto blog = blogService.getBlogBySlug(slug);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

}
