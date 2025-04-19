package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.dto.CategoryDto;
import com.example.projectBackEnd.dto.CategoryWithSubCategoryDto;
import com.example.projectBackEnd.dto.SubCategoryDto;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.entity.SubCategory;
import com.example.projectBackEnd.repo.CategoryRepo;
import com.example.projectBackEnd.repo.SubCategoryRepo;
import com.example.projectBackEnd.service.CategoryService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepo categoryRepo, SubCategoryRepo subCategoryRepo) {
        this.categoryRepo = categoryRepo;
        this.subCategoryRepo = subCategoryRepo;
    }

    @Override
    public CommonResponse addCategory(CategoryDto categoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = categoryValidation(categoryDto);
            if (!validationList.isEmpty()) {
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }

            Category category = castCategoryDtoToEntity(categoryDto);
            category = categoryRepo.save(category);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(category));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> addCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the category."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllCategories() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> categoryDtoList = categoryRepo.findAll().stream()
                    .filter(category -> category.getCommonStatus() == CommonStatus.ACTIVE)
                    .map(this::castCategoryEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(categoryDtoList);
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> getAllCategories()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching categories."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse updateCategory(CategoryDto categoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (categoryDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Category ID is required for update."));
                return commonResponse;
            }

            Category existingCategory = categoryRepo.findById(categoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            existingCategory.setName(categoryDto.getName());
            existingCategory.setDescription(categoryDto.getDescription());
            existingCategory.setImage(categoryDto.getImage());
            existingCategory.setCommonStatus(categoryDto.getCommonStatus());

            categoryRepo.save(existingCategory);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingCategory));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> updateCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the category."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse deleteCategory(CategoryDto categoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (categoryDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Category ID is required for deletion."));
                return commonResponse;
            }

            Category existingCategory = categoryRepo.findById(categoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            existingCategory.setCommonStatus(CommonStatus.DELETED);
            categoryRepo.save(existingCategory);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingCategory));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> deleteCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while deleting the category."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getCategoriesByIds(List<Long> categoryIds) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Category> categoriesList = categoryRepo.findAllById(categoryIds);

            List<CategoryDto> categoryDtoList = categoriesList.stream()
                    .map(this::castCategoryEntityToDto)
                    .collect(Collectors.toList());

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(categoryDtoList));
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching categories."));
        }
        return commonResponse;
    }

    @Override
    public List<Category> searchByName(String name) {
        return categoryRepo.findByNameContainingIgnoreCase(name);
    }

    @Override
    public CommonResponse getCategoryById(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<Category> categoryOptional = categoryRepo.findById(id);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                CategoryDto categoryDto = castCategoryEntityToDto(category);
                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(categoryDto));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Category not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> getCategoryById()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching the category."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getSubcategoriesByCategoryId(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<Category> categoryOptional = categoryRepo.findById(id);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                List<SubCategory> subCategories = subCategoryRepo.findByCategoryAndCommonStatus(category, CommonStatus.ACTIVE);

                List<SubCategoryDto> subCategoryDtoList = subCategories.stream()
                        .map(this::castSubCategoryEntityToDto)
                        .collect(Collectors.toList());

                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(subCategoryDtoList));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Category not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in CategoryService -> getSubcategoriesByCategoryId()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching subcategories."));
        }
        return commonResponse;
    }

    private Category castCategoryDtoToEntity(CategoryDto categoryDto) {
        Category category = new Category();
        if (categoryDto.getId() != null) {
            category.setId(categoryDto.getId());
        }
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImage(categoryDto.getImage());
        category.setCommonStatus(categoryDto.getCommonStatus() != null ? categoryDto.getCommonStatus() : CommonStatus.ACTIVE);
        return category;
    }

    @Override
    public CommonResponse getAllCategoriesWithSubcategories() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Category> categories = categoryRepo.findAll();
            List<CategoryWithSubCategoryDto> categoryDtos = new ArrayList<>();

            for (Category category : categories) {
                CategoryWithSubCategoryDto categoryDto = new CategoryWithSubCategoryDto();
                categoryDto.setId(category.getId());
                categoryDto.setName(category.getName());
                categoryDto.setDescription(category.getDescription());
                categoryDto.setCommonStatus(category.getCommonStatus());

                // Get subcategories for this category
                List<SubCategory> subcategories = subCategoryRepo.findByCategoryId(category.getId());
                List<SubCategoryDto> subCategoryDtos = subcategories.stream()
                        .map(subCategory -> {
                            SubCategoryDto subCategoryDto = new SubCategoryDto();
                            subCategoryDto.setId(subCategory.getId());
                            subCategoryDto.setName(subCategory.getName());
                            subCategoryDto.setDescription(subCategory.getDescription());
                            subCategoryDto.setCommonStatus(subCategory.getCommonStatus());
                            subCategoryDto.setCategoryId(category.getId());
                            return subCategoryDto;
                        })
                        .collect(Collectors.toList());

                categoryDto.setSubCategories(subCategoryDtos);
                categoryDtos.add(categoryDto);
            }

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(categoryDtos));
            return commonResponse;
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching categories with subcategories."+ e.getMessage()));
            return commonResponse;
        }
    }


    private CategoryDto castCategoryEntityToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setImage(category.getImage());
        categoryDto.setCommonStatus(category.getCommonStatus());
        return categoryDto;
    }

    private SubCategoryDto castSubCategoryEntityToDto(SubCategory subCategory) {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(subCategory.getId());
        subCategoryDto.setName(subCategory.getName());
        subCategoryDto.setDescription(subCategory.getDescription());
        subCategoryDto.setImage(subCategory.getImage());
        subCategoryDto.setCommonStatus(subCategory.getCommonStatus());
        subCategoryDto.setCategoryId(subCategory.getCategory().getId());
        return subCategoryDto;
    }

    private List<String> categoryValidation(CategoryDto categoryDto) {
        List<String> validationList = new ArrayList<>();
        if (CommonValidation.stringNullValidation(categoryDto.getName())) {
            validationList.add(CommonMsg.EMPTY_CATEGORY_NAME);
        }
        if (CommonValidation.stringNullValidation(categoryDto.getDescription())) {
            validationList.add(CommonMsg.EMPTY_CATEGORY_DESCRIPTION);
        }
        return validationList;
    }
}
