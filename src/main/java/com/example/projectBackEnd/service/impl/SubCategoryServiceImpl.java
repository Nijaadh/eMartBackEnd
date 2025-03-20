package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.dto.SubCategoryDto;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.SubCategory;
import com.example.projectBackEnd.repo.CategoryRepo;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.repo.SubCategoryRepo;
import com.example.projectBackEnd.service.SubCategoryService;
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
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepo subCategoryRepo;
    private final CategoryRepo categoryRepo;
    private final ItemsRepo itemsRepo;

    @Autowired
    public SubCategoryServiceImpl(SubCategoryRepo subCategoryRepo, CategoryRepo categoryRepo, ItemsRepo itemsRepo) {
        this.subCategoryRepo = subCategoryRepo;
        this.categoryRepo = categoryRepo;
        this.itemsRepo = itemsRepo;
    }

    @Override
    public CommonResponse addSubCategory(SubCategoryDto subCategoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = subCategoryValidation(subCategoryDto);
            if (!validationList.isEmpty()) {
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }

            SubCategory subCategory = castSubCategoryDtoToEntity(subCategoryDto);
            subCategory = subCategoryRepo.save(subCategory);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(subCategory));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> addSubCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the subcategory."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllSubCategories() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> subCategoryDtoList = subCategoryRepo.findAll().stream()
                    .filter(subCategory -> subCategory.getCommonStatus() == CommonStatus.ACTIVE)
                    .map(this::castSubCategoryEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(subCategoryDtoList);
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> getAllSubCategories()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching subcategories."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse updateSubCategory(SubCategoryDto subCategoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (subCategoryDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("SubCategory ID is required for update."));
                return commonResponse;
            }

            SubCategory existingSubCategory = subCategoryRepo.findById(subCategoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));

            existingSubCategory.setName(subCategoryDto.getName());
            existingSubCategory.setDescription(subCategoryDto.getDescription());
            existingSubCategory.setImage(subCategoryDto.getImage());
            existingSubCategory.setCommonStatus(subCategoryDto.getCommonStatus());

            if (subCategoryDto.getCategoryId() != null) {
                Category category = categoryRepo.findById(subCategoryDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                existingSubCategory.setCategory(category);
            }

            subCategoryRepo.save(existingSubCategory);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingSubCategory));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> updateSubCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the subcategory."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse deleteSubCategory(SubCategoryDto subCategoryDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (subCategoryDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("SubCategory ID is required for deletion."));
                return commonResponse;
            }

            SubCategory existingSubCategory = subCategoryRepo.findById(subCategoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));

            existingSubCategory.setCommonStatus(CommonStatus.DELETED);
            subCategoryRepo.save(existingSubCategory);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingSubCategory));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> deleteSubCategory()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while deleting the subcategory."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getSubCategoriesByIds(List<Long> subCategoryIds) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<SubCategory> subCategoriesList = subCategoryRepo.findAllById(subCategoryIds);

            List<SubCategoryDto> subCategoryDtoList = subCategoriesList.stream()
                    .map(this::castSubCategoryEntityToDto)
                    .collect(Collectors.toList());

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(subCategoryDtoList));
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching subcategories."));
        }
        return commonResponse;
    }

    @Override
    public List<SubCategory> searchByName(String name) {
        return subCategoryRepo.findByNameContainingIgnoreCase((name));
    }

    @Override
    public CommonResponse getSubCategoryById(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findById(id);
            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();
                SubCategoryDto subCategoryDto = castSubCategoryEntityToDto(subCategory);
                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(subCategoryDto));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("SubCategory not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> getSubCategoryById()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching the subcategory."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getSubCategoriesByCategoryId(Long categoryId) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
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
                commonResponse.setErrorMessages(Collections.singletonList("Category not found with ID: " + categoryId));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> getSubCategoriesByCategoryId()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching subcategories."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getItemsBySubCategoryId(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findById(id);
            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();
                List<Items> items = itemsRepo.findBySubCategoryAndCommonStatus(subCategory, CommonStatus.ACTIVE);

                List<ItemsDto> itemsDtoList = items.stream()
                        .map(this::castItemsEntityToDto)
                        .collect(Collectors.toList());

                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(itemsDtoList));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("SubCategory not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in SubCategoryService -> getItemsBySubCategoryId()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching items."));
        }
        return commonResponse;
    }

    private SubCategory castSubCategoryDtoToEntity(SubCategoryDto subCategoryDto) {
        SubCategory subCategory = new SubCategory();
        if (subCategoryDto.getId() != null) {
            subCategory.setId(subCategoryDto.getId());
        }
        subCategory.setName(subCategoryDto.getName());
        subCategory.setDescription(subCategoryDto.getDescription());
        subCategory.setImage(subCategoryDto.getImage());
        subCategory.setCommonStatus(subCategoryDto.getCommonStatus() != null ? subCategoryDto.getCommonStatus() : CommonStatus.ACTIVE);

        if (subCategoryDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(subCategoryDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            subCategory.setCategory(category);
        }

        return subCategory;
    }

    private SubCategoryDto castSubCategoryEntityToDto(SubCategory subCategory) {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(subCategory.getId());
        subCategoryDto.setName(subCategory.getName());
        subCategoryDto.setDescription(subCategory.getDescription());
        subCategoryDto.setImage(subCategory.getImage());
        subCategoryDto.setCommonStatus(subCategory.getCommonStatus());
        if (subCategory.getCategory() != null) {
            subCategoryDto.setCategoryId(subCategory.getCategory().getId());
        }
        return subCategoryDto;
    }

    private ItemsDto castItemsEntityToDto(Items items) {
        ItemsDto itemsDto = new ItemsDto();
        itemsDto.setId(String.valueOf(items.getId()));
        itemsDto.setName(items.getName());
        itemsDto.setDescription(items.getDescription());
        itemsDto.setImage(items.getImage());
        itemsDto.setUnitPrice(items.getUnitPrice().toString());
        itemsDto.setCommonStatus(items.getCommonStatus());
        return itemsDto;
    }

    private List<String> subCategoryValidation(SubCategoryDto subCategoryDto) {
        List<String> validationList = new ArrayList<>();
        if (CommonValidation.stringNullValidation(subCategoryDto.getName())) {
            validationList.add(CommonMsg.EMPTY_SUBCATEGORY_NAME);
        }
        if (CommonValidation.stringNullValidation(subCategoryDto.getDescription())) {
            validationList.add(CommonMsg.EMPTY_SUBCATEGORY_DESCRIPTION);
        }
        if (subCategoryDto.getCategoryId() == null) {
            validationList.add(CommonMsg.EMPTY_CATEGORY_ID);
        }
        return validationList;
    }
}
