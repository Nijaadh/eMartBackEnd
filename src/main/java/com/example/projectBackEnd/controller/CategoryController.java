package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.CategoryDto;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.service.CategoryService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public CommonResponse addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/getAll")
    public CommonResponse getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/update")
    public CommonResponse updateCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @PutMapping("/delete")
    public CommonResponse deleteCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.deleteCategory(categoryDto);
    }

    @PostMapping("/by-ids")
    public CommonResponse getCategoriesByIds(@RequestBody List<Long> categoryIds) {
        return categoryService.getCategoriesByIds(categoryIds);
    }

    @GetMapping("/search")
    public List<Category> searchByName(@RequestParam String name) {
        return categoryService.searchByName(name);
    }

    @GetMapping("/{id}")
    public CommonResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/subcategories")
    public CommonResponse getSubcategoriesByCategoryId(@PathVariable Long id) {
        return categoryService.getSubcategoriesByCategoryId(id);
    }

    @GetMapping("/with-subcategories")
    public CommonResponse getAllCategoriesWithSubcategories() {
        return categoryService.getAllCategoriesWithSubcategories();
    }
}
