package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.util.CommonResponse;

import java.util.List;

public interface ItemService {
    CommonResponse addItems(ItemsDto itemsDto);

    CommonResponse getAllItems();

    CommonResponse updateItems(ItemsDto itemsDto);

    CommonResponse deleteItem(ItemsDto itemsDto);

    CommonResponse getItemsByIds(List<Long> itemIds);

    List<Items> searchByName(String name);
}
