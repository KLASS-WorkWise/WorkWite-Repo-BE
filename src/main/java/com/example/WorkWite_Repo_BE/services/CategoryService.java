package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.CategoryDto.CategoryResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CategoryDto.CreatCategoryRequestDto;
import com.example.WorkWite_Repo_BE.dtos.CategoryDto.UpdateCategoryDto;
import com.example.WorkWite_Repo_BE.entities.Category;
import com.example.WorkWite_Repo_BE.repositories.CategoryJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryService {
    private final CategoryJpaRepository categoryRepository;

    public CategoryService(CategoryJpaRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDto convertDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getBlog()
        );
    }

    // creat
    public CategoryResponseDto creatCategory(CreatCategoryRequestDto creatCategoryRequestDto) {
        Category category = new Category();
        category.setName(creatCategoryRequestDto.getName());
        category.setDescription(creatCategoryRequestDto.getDescription());
        Category newCategory = categoryRepository.save(category);
        return convertDto(newCategory);
    }

    // lay tat ca
    public List<CategoryResponseDto> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::convertDto).toList();
    }

    // lay theo id
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) return null;
        return convertDto(category);
    }
    // xoa theo id
    public boolean deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }
    // update
    public CategoryResponseDto updateCategory(Long id, UpdateCategoryDto requestDto) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return null;
        }
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        return convertDto(updatedCategory);
    }
}