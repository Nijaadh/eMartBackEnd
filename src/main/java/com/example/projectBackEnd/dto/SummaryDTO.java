package com.example.projectBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {
    private int totalItems;
    private int itemsInStock;
    private int todayOrders;
    private double todayRevenue;

}