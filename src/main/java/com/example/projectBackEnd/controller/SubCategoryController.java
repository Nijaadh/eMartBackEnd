package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.SubCategoryDto;
import com.example.projectBackEnd.entity.SubCategory;
import com.example.projectBackEnd.service.SubCategoryService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@CrossOrigin(origins = "http://localhost:4200")
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @Autowired
    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping("/add")
    public CommonResponse addSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        return subCategoryService.addSubCategory(subCategoryDto);
    }

    @GetMapping("/getAll")
    public CommonResponse getAllSubCategories() {
        return subCategoryService.getAllSubCategories();
    }

    @PutMapping("/update")
    public CommonResponse updateSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        return subCategoryService.updateSubCategory(subCategoryDto);
    }

    @PutMapping("/delete")
    public CommonResponse deleteSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        return subCategoryService.deleteSubCategory(subCategoryDto);
    }

    @PostMapping("/by-ids")
    public CommonResponse getSubCategoriesByIds(@RequestBody List<Long> subCategoryIds) {
        return subCategoryService.getSubCategoriesByIds(subCategoryIds);
    }

    @GetMapping("/search")
    public List<SubCategory> searchByName(@RequestParam String name) {
        return subCategoryService.searchByName(name);
    }

    @GetMapping("/{id}")
    public CommonResponse getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.getSubCategoryById(id);
    }

    @GetMapping("/by-category/{categoryId}")
    public CommonResponse getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        return subCategoryService.getSubCategoriesByCategoryId(categoryId);
    }

    @GetMapping("/{id}/items")
    public CommonResponse getItemsBySubCategoryId(@PathVariable Long id) {
        return subCategoryService.getItemsBySubCategoryId(id);
    }
}
