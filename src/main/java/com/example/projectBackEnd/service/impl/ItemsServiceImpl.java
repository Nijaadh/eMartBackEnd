package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.dto.ItemsDtoWithCatagoryNames;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.SubCategory;
import com.example.projectBackEnd.repo.CategoryRepo;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.repo.SubCategoryRepo;
import com.example.projectBackEnd.service.ItemService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class ItemsServiceImpl implements ItemService {
    private final ItemsRepo itemsRepo;
    private final SubCategoryRepo subCategoryRepo;
    private final CategoryRepo categoryRepo;

    @Autowired
    public ItemsServiceImpl(ItemsRepo itemsRepo, SubCategoryRepo subCategoryRepo, CategoryRepo categoryRepo) {
        this.itemsRepo = itemsRepo;
        this.subCategoryRepo = subCategoryRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public CommonResponse addItems(ItemsDto itemsDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = itemsValidation(itemsDto);
            if (!validationList.isEmpty()) {
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }

            Items items = castItemsDtoToEntity(itemsDto);
            items.setCommonStatus(CommonStatus.ACTIVE);

            items = itemsRepo.save(items);

            // Convert to the new DTO with category and subcategory names
            ItemsDtoWithCatagoryNames itemWithNames = castItemsEntityToDtoWithNames(items);

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemWithNames));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in ProductService -> saveProduct()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the product."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllItems() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> itemDtoList = itemsRepo.findAll().stream()
                    .filter(items -> items.getCommonStatus() == CommonStatus.ACTIVE)
                    .map(this::castItemsEntityToDtoWithNames)  // Use the new mapping method
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(itemDtoList);  // Directly set the list
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in ItemService -> getAllItems()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching items."));
        }
        return commonResponse;
    }

    
    @Override
    public CommonResponse updateItems(ItemsDto itemsDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (itemsDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Item ID is required for update."));
                return commonResponse;
            }
            Items existingItem = itemsRepo.findById(Long.valueOf(itemsDto.getId()))
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            existingItem.setName(itemsDto.getName());
            existingItem.setDescription(itemsDto.getDescription());
            existingItem.setUnitPrice(Double.valueOf(itemsDto.getUnitPrice()));
            existingItem.setCommonStatus(itemsDto.getCommonStatus());
            existingItem.setItemCount(itemsDto.getItemCount());
            existingItem.setSalesCount(itemsDto.getSalesCount());
            existingItem.setDiscount(itemsDto.getDiscount() != null ? Double.valueOf(itemsDto.getDiscount()) : null);
            existingItem.setReOrderLevel(itemsDto.getReOrderLevel());

            // Note: We don't update createdAt during updates
            // The createdAt field should remain as it was when the item was first created

            if (itemsDto.getSubCategoryId() != null) {
                SubCategory subCategory = subCategoryRepo.findById(itemsDto.getSubCategoryId())
                        .orElse(null);
                existingItem.setSubCategory(subCategory);
            }
            Items updatedItem = itemsRepo.save(existingItem);
            // Convert to the new DTO with category and subcategory names
            ItemsDtoWithCatagoryNames itemWithNames = castItemsEntityToDtoWithNames(updatedItem);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemWithNames));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in ProductService -> updateProduct()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the product."));
        }
        return commonResponse;
    }


    @Override
    public CommonResponse deleteItem(ItemsDto itemsDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (itemsDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Item ID is required for update."));
                return commonResponse;
            }

            Items existingItem = itemsRepo.findById(Long.valueOf(itemsDto.getId()))
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            existingItem.setCommonStatus(CommonStatus.DELETED);
            Items deletedItem = itemsRepo.save(existingItem);

            // Convert to the new DTO with category and subcategory names
            ItemsDtoWithCatagoryNames itemWithNames = castItemsEntityToDtoWithNames(deletedItem);

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemWithNames));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in ItemService -> deleteProduct()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while deleting the item."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getItemsByIds(List<Long> itemIds) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            // Fetch items by the given list of item IDs
            List<Items> itemsList = itemsRepo.findAllById(itemIds);

            // Convert entities to DTOs with category and subcategory names
            List<ItemsDtoWithCatagoryNames> itemsDtoList = itemsList.stream()
                    .map(this::castItemsEntityToDtoWithNames)
                    .collect(Collectors.toList());

            // Set response status and payload
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemsDtoList));
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching items."));
            //e.printStackTrace();
        }
        return commonResponse;
    }

    @Override
    public CommonResponse searchByName(String name) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Items> items = itemsRepo.findByNameContainingIgnoreCase(name);
            List<ItemsDtoWithCatagoryNames> itemsDtoList = items.stream()
                    .map(this::castItemsEntityToDtoWithNames)
                    .collect(Collectors.toList());

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemsDtoList));
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while searching for items."));
        }
        return commonResponse;
    }

    private Items castItemsDtoToEntity(ItemsDto itemsDto){
        Items items = new Items();
        items.setName(itemsDto.getName());
        items.setDescription(itemsDto.getDescription());
        items.setUnitPrice(Double.valueOf(itemsDto.getUnitPrice()));
        items.setCommonStatus(itemsDto.getCommonStatus());
        items.setCategory(itemsDto.getCategory());
        items.setImage(itemsDto.getImage());
        items.setItemCount(itemsDto.getItemCount());
        items.setSalesCount(itemsDto.getSalesCount());
        items.setDiscount(itemsDto.getDiscount() != null ? Double.valueOf(itemsDto.getDiscount()) : null);
        items.setReOrderLevel(itemsDto.getReOrderLevel());

        // Set the createdAt field to the current date and time
        items.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        if (itemsDto.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepo.findById(itemsDto.getSubCategoryId())
                    .orElse(null);
            items.setSubCategory(subCategory);
        }
        return items;
    }


    private ItemsDto castItemsEntityToDto(Items items){
        ItemsDto itemsDto = new ItemsDto();
        itemsDto.setId(String.valueOf(items.getId()));
        itemsDto.setName(items.getName());
        itemsDto.setDescription(items.getDescription());
        itemsDto.setCategory(items.getCategory());
        itemsDto.setImage(items.getImage());
        itemsDto.setUnitPrice(items.getUnitPrice().toString());
        itemsDto.setCommonStatus(items.getCommonStatus());
        itemsDto.setItemCount(items.getItemCount());
        itemsDto.setSalesCount(items.getSalesCount());
        itemsDto.setDiscount(items.getDiscount() != null ? items.getDiscount().toString() : null);
        itemsDto.setReOrderLevel(items.getReOrderLevel());

        // Set the createdAt field
        itemsDto.setCreatedAt(items.getCreatedAt());

        if (items.getSubCategory() != null) {
            itemsDto.setSubCategoryId(items.getSubCategory().getId());
        }
        return itemsDto;
    }

    // New method to map entity to DTO with category and subcategory names
    private ItemsDtoWithCatagoryNames castItemsEntityToDtoWithNames(Items items) {
        ItemsDtoWithCatagoryNames dto = new ItemsDtoWithCatagoryNames();
        dto.setId(String.valueOf(items.getId()));
        dto.setName(items.getName());
        dto.setDescription(items.getDescription());
        dto.setCategory(items.getCategory());
        dto.setImage(items.getImage());
        dto.setUnitPrice(items.getUnitPrice().toString());
        dto.setCommonStatus(items.getCommonStatus());
        dto.setItemCount(items.getItemCount());
        dto.setSalesCount(items.getSalesCount());
        dto.setDiscount(items.getDiscount() != null ? items.getDiscount().toString() : null);
        dto.setReOrderLevel(items.getReOrderLevel());

        // Set the createdAt field
        dto.setCreatedAt(items.getCreatedAt());

        // Get subcategory and its name
        if (items.getSubCategory() != null) {
            SubCategory subCategory = items.getSubCategory();
            dto.setSubCategoryId(subCategory.getId());
            dto.setSubCategoryName(subCategory.getName());
            // Get category name from subcategory's category
            if (subCategory.getCategory() != null) {
                dto.setCatagoryName(subCategory.getCategory().getName());
            } else {
                // If category is not available in subcategory, try to find it by ID from the category field
                try {
                    Long categoryId = Long.valueOf(items.getCategory());
                    Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
                    categoryOpt.ifPresent(category -> dto.setCatagoryName(category.getName()));
                } catch (NumberFormatException e) {
                    // If category is not a number, use it as is
                    dto.setCatagoryName(items.getCategory());
                }
            }
        } else {
            // If subcategory is not available, try to find category by ID
            try {
                Long categoryId = Long.valueOf(items.getCategory());
                Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
                categoryOpt.ifPresent(category -> dto.setCatagoryName(category.getName()));
            } catch (NumberFormatException e) {
                // If category is not a number, use it as is
                dto.setCatagoryName(items.getCategory());
            }
        }
        return dto;
    }

    private List<String> itemsValidation(ItemsDto itemsDto) {
        List<String> validationList = new ArrayList<>();
        if (CommonValidation.stringNullValidation(itemsDto.getName())) {
            validationList.add(CommonMsg.EMPTY_PRODUCT_NAME);
        }
        if (CommonValidation.stringNullValidation(itemsDto.getDescription())) {
            validationList.add(CommonMsg.EMPTY_PRODUCT_DESCRIPTION);
        }
        if (CommonValidation.stringNullValidation(itemsDto.getUnitPrice())) {
            validationList.add(CommonMsg.EMPTY_PRICE);
        }
        return validationList;
    }
}
