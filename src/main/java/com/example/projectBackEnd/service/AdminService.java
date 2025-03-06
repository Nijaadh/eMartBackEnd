package com.example.projectBackEnd.service;

import java.util.List;

public interface AdminService {
    long getUserCount();

    long getItemCount();

    long countActiveGifts();

    long countProcessingGifts();

    long countDeliveredGifts();

    Double getTotalPriceForPaidGifts();

    Double[] getLastYearMonthlyTotalPrice();
}
