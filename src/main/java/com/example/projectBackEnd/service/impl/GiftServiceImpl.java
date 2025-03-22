package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import com.example.projectBackEnd.dto.GiftDto;
import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.User;
import com.example.projectBackEnd.repo.GiftRepo;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.repo.UserRepo;
import com.example.projectBackEnd.service.EmailService;
import com.example.projectBackEnd.service.GiftService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.hibernate.tool.schema.SchemaToolingLogging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class GiftServiceImpl implements GiftService {

    private final GiftRepo giftRepo;
    private final ItemsRepo itemsRepo;
    private final EmailService emailService;
    private final UserRepo userRepo;

    @Autowired
    public GiftServiceImpl(GiftRepo giftRepo, ItemsRepo itemsRepo, UserRepo userRepo,EmailService emailService) {
        this.giftRepo = giftRepo;
        this.itemsRepo = itemsRepo;
        this.userRepo = userRepo;
        this.emailService=emailService;
    }

    @Override
    public CommonResponse createGift(GiftDto giftDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = giftValidation(giftDto);
            if (!validationList.isEmpty()){
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }
            Gift gift = castGiftDtoToEntity(giftDto);
            gift = giftRepo.save(gift);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(gift.getId()));

            User user = userRepo.findById(Long.valueOf(gift.getUserId())).get();
            emailService.sendOrderConfirmationEmail(gift,user);

        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the gift."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllGift() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> giftDtoList = giftRepo.findAll().stream()
                    .filter(Gift -> Gift.getCommonStatus() == CommonStatus.ACTIVE && Gift.getPaymentStatus()==PaymentStatus.PAID)
                    .map(this::castEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(giftDtoList);  // Directly set the list
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in GiftService -> getAllGifts()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching gifts."));
        }
        return commonResponse;
    }

    public CommonResponse getAllGiftAccepted() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> giftDtoList = giftRepo.findAll().stream()
                    .filter(Gift -> Gift.getCommonStatus() == CommonStatus.INACTIVE && Gift.getPaymentStatus()==PaymentStatus.PAID)
                    .map(this::castEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(giftDtoList);  // Directly set the list
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in GiftService -> getAllGifts()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching gifts."));
        }
        return commonResponse;
    }
    public CommonResponse getAllGiftDelivered() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> giftDtoList = giftRepo.findAll().stream()
                    .filter(Gift -> Gift.getCommonStatus() == CommonStatus.DELETED && Gift.getPaymentStatus()==PaymentStatus.PAID)
                    .map(this::castEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(giftDtoList);  // Directly set the list
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in GiftService -> getAllGifts()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching gifts."));
        }
        return commonResponse;
    }

    @Override
    public List<Gift> getGift() {
        return giftRepo.findAll();
    }

    @Override
    public CommonResponse updatePaymentStatus(GiftDto giftDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (giftDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Gift ID is required for update."));
                return commonResponse;
            }

            Gift existingGiftBox = giftRepo.findById(Long.valueOf(giftDto.getId()))
                    .orElseThrow(() -> new RuntimeException("Gift Box not found"));

            existingGiftBox.setPaymentStatus(giftDto.getPaymentStatus());


            Gift gift = giftRepo.findById(Long.valueOf(giftDto.getId())).orElse(null);
            if (gift != null) {
                User user = userRepo.findById(Long.valueOf(gift.getUserId())).orElse(null);
                if (user != null) {
                    emailService.sendOrderConfirmationEmail(gift, user);
                }
            }


        } catch (Exception e) {
            LOGGER.error("/**************** Exception in GiftService -> updateProduct()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the gift payment."));
        }
        return commonResponse;
    }
    public CommonResponse updateCommonStatus(GiftDto giftDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (giftDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Gift ID is required for update."));
                return commonResponse;
            }

            Gift existingGiftBox = giftRepo.findById(Long.valueOf(giftDto.getId()))
                    .orElseThrow(() -> new RuntimeException("Gift Box not found"));
             existingGiftBox.setCommonStatus(giftDto.getCommonStatus());

            giftRepo.save(existingGiftBox);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList("Status update SuccessFully"));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in GiftService -> updateStatus()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the gift Status."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllGiftByUserId(String userId) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            // Fetch gifts by user ID and filter only ACTIVE gifts
            List<Gift> giftList = giftRepo.findByUserId(userId).stream()
                    .filter(Gift -> Gift.getCommonStatus() == CommonStatus.ACTIVE)
                    .collect(Collectors.toList());

            // If gifts are found, return a success response with gift details
            if (!giftList.isEmpty()) {
                List<GiftDto> giftDtoList = giftList.stream()
                        .map(this::castEntityToDto)  // Converting Gift entity to DTO
                        .collect(Collectors.toList());
                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(giftDtoList));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("No active gifts found for the user."));
            }
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in GiftService -> getGiftDetailsByUserId()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching gifts for the user."));
        }
        return commonResponse;
    }

    private Gift castGiftDtoToEntity(GiftDto giftDto) {
        Gift gift = new Gift();
        gift.setGiftName(giftDto.getGiftName());
        gift.setCreatedAt(giftDto.getCreatedAt());
        gift.setCommonStatus(giftDto.getCommonStatus());
        gift.setUserId(giftDto.getUserId());
        gift.setRecieverAddress(giftDto.getRecieverAddress());
        gift.setSendingDate(giftDto.getSendingDate());
        gift.setZip(giftDto.getZip());
        gift.setTotalPrice(Double.valueOf(giftDto.getTotalPrice()));
        gift.setPaymentStatus(PaymentStatus.PAID);
        Set<Items> items = giftDto.getItemIds().stream()
                .map(itemId -> itemsRepo.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Item not found")))
                .collect(Collectors.toSet());
        gift.setItems(items);

        return gift;
    }
    private GiftDto castEntityToDto(Gift gift) {
        GiftDto giftDto = new GiftDto();
        giftDto.setId(gift.getId());
        giftDto.setGiftName(gift.getGiftName());
        giftDto.setSendingDate(gift.getSendingDate());
        giftDto.setCreatedAt(gift.getCreatedAt());
        giftDto.setCommonStatus(gift.getCommonStatus());
        giftDto.setUserId(gift.getUserId());
        giftDto.setRecieverAddress(gift.getRecieverAddress());
        giftDto.setZip(gift.getZip());
        giftDto.setPaymentStatus(gift.getPaymentStatus());
        giftDto.setTotalPrice(String.valueOf(gift.getTotalPrice()));
        giftDto.setItemIds(gift.getItems().stream()
                .map(Items::getId)
                .collect(Collectors.toSet()));
        return giftDto;
    }


    private List<String> giftValidation(GiftDto giftDto) {
        List<String> validationList = new ArrayList<>();

        if (CommonValidation.stringNullValidation(giftDto.getGiftName())) {
            validationList.add(CommonMsg.EMPTY_GIFT_NAME);
        }

        return validationList;
    }
}
