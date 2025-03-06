package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.GiftDto;
import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.util.CommonResponse;

import java.util.List;

public interface GiftService {
    CommonResponse createGift(GiftDto giftDto);
    CommonResponse getAllGift();
    CommonResponse getAllGiftAccepted();
    CommonResponse getAllGiftDelivered();
    public List<Gift> getGift();

    CommonResponse updatePaymentStatus(GiftDto giftDto);

    CommonResponse getAllGiftByUserId(String userId);
    CommonResponse updateCommonStatus(GiftDto giftDto);
}
