package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.OrderStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String createdAt;
    private String receiverAddress;
    private String totalPrice;
    private String zip;
    private CommonStatus commonStatus;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String userId;
    private Map<Long, Integer> itemQuantities; // Map of item ID to quantity
    private List<Map<String, Object>> itemDetails; // Added field for detailed item information
}
