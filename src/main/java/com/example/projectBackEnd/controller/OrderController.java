package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.OrderDto;
import com.example.projectBackEnd.entity.Order;
import com.example.projectBackEnd.service.OrderService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public CommonResponse createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    @GetMapping("/all")
    public CommonResponse getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/pending")
    public CommonResponse getAllPendingOrders() {
        return orderService.getAllOrdersPending();
    }

    @GetMapping("/processing")
    public CommonResponse getAllProcessingOrders() {
        return orderService.getAllOrdersProcessing();
    }

    @GetMapping("/shipped")
    public CommonResponse getAllShippedOrders() {
        return orderService.getAllOrdersShipped();
    }

    @GetMapping("/delivered")
    public CommonResponse getAllDeliveredOrders() {
        return orderService.getAllOrdersDelivered();
    }

    @GetMapping("/list")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @PutMapping("/update-payment-status")
    public CommonResponse updatePaymentStatus(@RequestBody OrderDto orderDto) {
        return orderService.updatePaymentStatus(orderDto);
    }

    @PutMapping("/update-order-status")
    public CommonResponse updateOrderStatus(@RequestBody OrderDto orderDto) {
        return orderService.updateOrderStatus(orderDto);
    }

    @GetMapping("/user/{userId}")
    public CommonResponse getOrdersByUserId(@PathVariable String userId) {
        return orderService.getAllOrdersByUserId(userId);
    }
}
