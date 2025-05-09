package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.OrderDto;
import com.example.projectBackEnd.dto.OrderResponseDTO;
import com.example.projectBackEnd.entity.Order;
import com.example.projectBackEnd.util.CommonResponse;

import java.util.List;

public interface OrderService {
    CommonResponse createOrder(OrderDto orderDto);
    CommonResponse getAllOrders();
    CommonResponse getAllOrdersPending();
    CommonResponse getAllOrdersProcessing();
    CommonResponse getAllOrdersShipped();
    CommonResponse getAllOrdersDelivered();
    List<Order> getOrders();
    List<OrderResponseDTO> getOrdersAsDTO();
    CommonResponse updatePaymentStatus(OrderDto orderDto);
    CommonResponse updateOrderStatus(OrderDto orderDto);
    CommonResponse getAllOrdersByUserId(String userId);
}
