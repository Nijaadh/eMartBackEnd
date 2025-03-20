package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.SubCategoryDto;
import com.example.projectBackEnd.entity.SubCategory;
import com.example.projectBackEnd.util.CommonResponse;

import java.util.List;

public interface SubCategoryService {
    CommonResponse addSubCategory(SubCategoryDto subCategoryDto);
    CommonResponse getAllSubCategories();
    CommonResponse updateSubCategory(SubCategoryDto subCategoryDto);
    CommonResponse deleteSubCategory(SubCategoryDto subCategoryDto);
    CommonResponse getSubCategoriesByIds(List<Long> subCategoryIds);
    List<SubCategory> searchByName(String name);
    CommonResponse getSubCategoryById(Long id);
    CommonResponse getSubCategoriesByCategoryId(Long categoryId);
    CommonResponse getItemsBySubCategoryId(Long id);
}
