package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import com.example.projectBackEnd.repo.GiftRepo;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.repo.UserRepo;
import com.example.projectBackEnd.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepo userRepo;
    private final ItemsRepo itemsRepo;
    private final GiftRepo giftRepo;



    @Autowired
    public AdminServiceImpl(UserRepo userRepo, ItemsRepo itemsRepo, GiftRepo giftRepo) {
        this.userRepo = userRepo;
        this.itemsRepo = itemsRepo;
        this.giftRepo = giftRepo;
    }

    @Override
    public long getUserCount() {
        return userRepo.count();
    }

    @Override
    public long getItemCount() {
        return itemsRepo.count();
    }

    @Override
    public long countActiveGifts() {
        return giftRepo.countByCommonStatus(CommonStatus.ACTIVE);
    }

    @Override
    public long countProcessingGifts() {
        return giftRepo.countByCommonStatus(CommonStatus.INACTIVE);
    }

    @Override
    public long countDeliveredGifts() {
        return giftRepo.countByCommonStatus(CommonStatus.DELETED);
    }

    @Override
    public Double getTotalPriceForPaidGifts() {
        return giftRepo.getTotalPriceByPaymentStatus(PaymentStatus.PAID);
    }

    @Override
    public Double[] getLastYearMonthlyTotalPrice() {
        List<Object[]> results = giftRepo.findMonthlyIncomeForCurrentYear();

        // Initialize an array of 12 elements (Jan to Dec) with 0.0 as default
        Double[] monthlyIncome = new Double[12];
        Arrays.fill(monthlyIncome, 0.0);

        // Populate the array with the results from the query
        for (Object[] result : results) {
            int month = (int) result[0];  // Month (1 = Jan, 2 = Feb, etc.)
            Double totalPrice = (Double) result[1];  // Total price for that month

            // Subtract 1 from month to fit into the 0-indexed array (0 = Jan, ..., 11 = Dec)
            monthlyIncome[month - 1] = totalPrice;
        }

        return monthlyIncome;
    }
}
