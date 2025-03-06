package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.GiftDto;
import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.service.GiftService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import java.util.List;

@RestController
@RequestMapping("/api/gift")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin
public class GiftController {
    private final GiftService giftService;

    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping("/create")
    public CommonResponse createGift(@RequestBody GiftDto giftDto){
        return giftService.createGift(giftDto);
    }
    @GetMapping("/getAllNew")
    public CommonResponse getAllGift(){
        return giftService.getAllGift();
    }
    @GetMapping("/getAllAcc")
    public CommonResponse getAllAccepted(){
        return giftService.getAllGiftAccepted();
    }
    @GetMapping("/getAllDeli")
    public CommonResponse getAllDelivered(){
        return giftService.getAllGiftDelivered();
    }
    @GetMapping("/get")
    public List<Gift> get(){
        return giftService.getGift();
    }
    @PostMapping("/addNew")
    public CommonResponse addNewGift(@RequestBody GiftDto giftDto){
        return giftService.createGift(giftDto);
    }

    @PutMapping("/paid")
    public CommonResponse updatePaymentStatus(@RequestBody GiftDto giftDto){
        return giftService.updatePaymentStatus(giftDto);
    }
    @PutMapping("/status")
    public CommonResponse updateCommonStatus(@RequestBody GiftDto giftDto){
        return giftService.updateCommonStatus(giftDto);
    }
    @GetMapping("/getAllByUser/{userId}")
    public CommonResponse getAllGiftByUserId(@PathVariable String userId){
        return giftService.getAllGiftByUserId(userId);
    }
}
