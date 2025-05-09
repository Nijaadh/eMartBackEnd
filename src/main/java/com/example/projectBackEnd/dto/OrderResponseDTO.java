package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.OrderStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private String createdAt;
    private String receiverAddress;
    private Double orderTotal;
    private String zip;
    private CommonStatus commonStatus;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String userId;
    private List<OrderItemResponseDTO> orderItems;
}
