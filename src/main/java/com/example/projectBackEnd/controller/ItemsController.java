package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.service.ItemService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemsController {
    private final ItemService itemService;

    @Autowired
    public ItemsController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/add")
    public CommonResponse addItems(@RequestBody ItemsDto itemsDto){
        return itemService.addItems(itemsDto);
    }
    @GetMapping("/getAll")
    public CommonResponse getAll(){
        return itemService.getAllItems();
    }
    @PutMapping("/update")
     public CommonResponse updateItems(@RequestBody ItemsDto itemsDto){
        return itemService.updateItems(itemsDto);
    }
    @PutMapping("/delete")
    public CommonResponse deleteItem(@RequestBody ItemsDto itemsDto){
        return itemService.deleteItem(itemsDto);
    }

    @PostMapping("/by-ids")
    public CommonResponse getItemsByIds(@RequestBody List<Long> itemIds) {
        return itemService.getItemsByIds(itemIds);
    }

    @GetMapping("/search")
    public List<Items> searchByName(@RequestParam String name) {
        return itemService.searchByName(name);
    }
}
