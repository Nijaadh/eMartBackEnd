package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.CategoryDto;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.util.CommonResponse;

import java.util.List;

public interface CategoryService {
    CommonResponse addCategory(CategoryDto categoryDto);
    CommonResponse getAllCategories();
    CommonResponse updateCategory(CategoryDto categoryDto);
    CommonResponse deleteCategory(CategoryDto categoryDto);
    CommonResponse getCategoriesByIds(List<Long> categoryIds);
    List<Category> searchByName(String name);
    CommonResponse getCategoryById(Long id);
    CommonResponse getSubcategoriesByCategoryId(Long id);
}
