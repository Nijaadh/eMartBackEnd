package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.service.ItemService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class ItemsServiceImpl implements ItemService {
    private final ItemsRepo itemsRepo;

    @Autowired
    public ItemsServiceImpl(ItemsRepo itemsRepo) {
        this.itemsRepo = itemsRepo;
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
            items = itemsRepo.save(items);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(items));
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
                    .filter(Items -> Items.getCommonStatus() == CommonStatus.ACTIVE)
                    .map(this::castItemsEntityToDto)
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

            itemsRepo.save(existingItem);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingItem));
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
            itemsRepo.save(existingItem);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(existingItem));
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

            // Convert entities to DTOs
            List<ItemsDto> itemsDtoList = itemsList.stream()
                    .map(this::castItemsEntityToDto)
                    .collect(Collectors.toList());

            // Set response status and payload
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(itemsDtoList));
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching items."));
          //  e.printStackTrace();
        }
        return commonResponse;
    }

    @Override
    public List<Items> searchByName(String name) {
        return itemsRepo.findByNameContainingIgnoreCase(name);
    }

    private Items castItemsDtoToEntity(ItemsDto itemsDto){
         Items items=new Items();
         items.setName(itemsDto.getName());
         items.setDescription(itemsDto.getDescription());
         items.setUnitPrice(Double.valueOf(itemsDto.getUnitPrice()));
         items.setCommonStatus(itemsDto.getCommonStatus());
         items.setCategory(itemsDto.getCategory());
         items.setImage(itemsDto.getImage());
        return items;
  }
  private ItemsDto castItemsEntityToDto(Items items){
        ItemsDto itemsDto=new ItemsDto();
        itemsDto.setId(String.valueOf(items.getId()));
        itemsDto.setName(items.getName());
        itemsDto.setDescription(items.getDescription());
        itemsDto.setCategory(items.getCategory());
        itemsDto.setImage(items.getImage());
        itemsDto.setUnitPrice(items.getUnitPrice().toString());
        itemsDto.setCommonStatus(items.getCommonStatus());
        return itemsDto;
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
