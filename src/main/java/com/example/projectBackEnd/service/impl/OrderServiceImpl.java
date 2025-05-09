package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.OrderStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import com.example.projectBackEnd.dto.*;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.Order;
import com.example.projectBackEnd.entity.OrderItemQuantity;
import com.example.projectBackEnd.entity.User;
import com.example.projectBackEnd.repo.ItemsRepo;
import com.example.projectBackEnd.repo.OrderItemQuantityRepo;
import com.example.projectBackEnd.repo.OrderRepo;
import com.example.projectBackEnd.repo.UserRepo;
import com.example.projectBackEnd.service.EmailService;
import com.example.projectBackEnd.service.OrderService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.hibernate.tool.schema.SchemaToolingLogging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final ItemsRepo itemsRepo;
    private final OrderItemQuantityRepo orderItemQuantityRepo;
    private final EmailService emailService;
    private final UserRepo userRepo;

    @Autowired
    public OrderServiceImpl(OrderRepo orderRepo, ItemsRepo itemsRepo, OrderItemQuantityRepo orderItemQuantityRepo, UserRepo userRepo, EmailService emailService) {
        this.orderRepo = orderRepo;
        this.itemsRepo = itemsRepo;
        this.orderItemQuantityRepo = orderItemQuantityRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    @Override
    public CommonResponse createOrder(OrderDto orderDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = orderValidation(orderDto);
            if (!validationList.isEmpty()) {
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }

            // Check if there's enough inventory for all items
            if (!checkInventoryAvailability(orderDto.getItemQuantities())) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Some items are out of stock or don't have enough inventory"));
                return commonResponse;
            }

            Order order = castOrderDtoToEntity(orderDto);
            order = orderRepo.save(order);

            // Save order item quantities and update inventory
            saveOrderItemQuantitiesAndUpdateInventory(order, orderDto.getItemQuantities());

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(order.getId()));

            // Send email confirmation
            User user = userRepo.findById(Long.valueOf(order.getUserId())).orElse(null);
            if (user != null) {
                // You'll need to adapt your email service to handle orders
                // emailService.sendOrderConfirmationEmail(order, user);
            }
        } catch (Exception e) {
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the order: " + e.getMessage()));
            e.printStackTrace();
        }
        return commonResponse;
    }

    /**
     * Checks if there's enough inventory for all items in the order
     */
    private boolean checkInventoryAvailability(Map<Long, Integer> itemQuantities) {
        if (itemQuantities == null) {
            return false;
        }
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantityOrdered = entry.getValue();
            Items item = itemsRepo.findById(itemId).orElse(null);
            if (item == null || item.getItemCount() < quantityOrdered) {
                return false;
            }
        }
        return true;
    }

    /**
     * Saves order item quantities and updates inventory counts
     */
    private void saveOrderItemQuantitiesAndUpdateInventory(Order order, Map<Long, Integer> itemQuantities) {
        if (itemQuantities != null) {
            for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
                Long itemId = entry.getKey();
                Integer quantity = entry.getValue();
                Items item = itemsRepo.findById(itemId).orElse(null);
                if (item != null) {
                    // Create order item quantity record
                    OrderItemQuantity orderItemQty = new OrderItemQuantity();
                    orderItemQty.setOrder(order);
                    orderItemQty.setItem(item);
                    orderItemQty.setQuantity(quantity);
                    orderItemQuantityRepo.save(orderItemQty);

                    // Update inventory: reduce item count and increase sales count
                    item.setItemCount(item.getItemCount() - quantity);
                    item.setSalesCount(item.getSalesCount() + quantity);

                    // Check if item count is below reorder level
                    if (item.getItemCount() <= item.getReOrderLevel()) {
                        // You could implement a notification system here
                        LOGGER.info("Item " + item.getName() + " (ID: " + item.getId() + ") is below reorder level. Current count: " + item.getItemCount());
                    }

                    // Save the updated item
                    itemsRepo.save(item);
                }
            }
        }
    }

    private void saveOrderItemQuantities(Order order, Map<Long, Integer> itemQuantities) {
        if (itemQuantities != null) {
            for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
                Long itemId = entry.getKey();
                Integer quantity = entry.getValue();
                Items item = itemsRepo.findById(itemId).orElse(null);
                if (item != null) {
                    OrderItemQuantity orderItemQty = new OrderItemQuantity();
                    orderItemQty.setOrder(order);
                    orderItemQty.setItem(item);
                    orderItemQty.setQuantity(quantity);
                    orderItemQuantityRepo.save(orderItemQty);
                }
            }
        }
    }

    @Override
    public CommonResponse getAllOrders() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> orderDtoList = orderRepo.findAll().stream()
                    .map(this::castEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(orderDtoList);
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in OrderService -> getAllOrders()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching orders."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllOrdersPending() {
        return getOrdersByStatus(OrderStatus.PENDING);
    }

    @Override
    public CommonResponse getAllOrdersProcessing() {
        return getOrdersByStatus(OrderStatus.PROCESSING);
    }

    @Override
    public CommonResponse getAllOrdersShipped() {
        return getOrdersByStatus(OrderStatus.SHIPPED);
    }

    @Override
    public CommonResponse getAllOrdersDelivered() {
        return getOrdersByStatus(OrderStatus.DELEVERD);
    }

    private CommonResponse getOrdersByStatus(OrderStatus status) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> orderDtoList = orderRepo.findAll().stream()
                    .filter(order -> order.getOrderStatus() == status && order.getPaymentStatus() == PaymentStatus.PAID)
                    .map(this::castEntityToDto)
                    .collect(Collectors.toList());
            commonResponse.setStatus(true);
            commonResponse.setPayload(orderDtoList);
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in OrderService -> getOrdersByStatus()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching orders."));
        }
        return commonResponse;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepo.findAll();
    }

    @Override
    public List<OrderResponseDTO> getOrdersAsDTO() {
        List<Order> orders = orderRepo.findAll();
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setOrderTotal(order.getOrderTotal());
        dto.setZip(order.getZip());
        dto.setCommonStatus(order.getCommonStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setUserId(order.getUserId());

        if (order.getOrderItems() != null) {
            List<OrderItemResponseDTO> orderItems = order.getOrderItems().stream()
                    .map(this::convertToOrderItemResponseDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(orderItems);
        }

        return dto;
    }

    private OrderItemResponseDTO convertToOrderItemResponseDTO(OrderItemQuantity orderItemQuantity) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(orderItemQuantity.getId());
        dto.setQuantity(orderItemQuantity.getQuantity());

        if (orderItemQuantity.getItem() != null) {
            ItemResponseDTO itemDTO = new ItemResponseDTO();
            Items item = orderItemQuantity.getItem();

            itemDTO.setId(item.getId());
            itemDTO.setName(item.getName());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setDescription(item.getDescription());
            itemDTO.setCategory(item.getCategory());
            itemDTO.setImage(item.getImage());
            itemDTO.setCommonStatus(item.getCommonStatus());
            itemDTO.setItemCount(item.getItemCount());
            itemDTO.setSalesCount(item.getSalesCount());
            itemDTO.setDiscount(item.getDiscount());
            itemDTO.setReOrderLevel(item.getReOrderLevel());

            if (item.getSubCategory() != null) {
                itemDTO.setSubCategoryName(item.getSubCategory().getName());
            }

            dto.setItem(itemDTO);
        }

        return dto;
    }

    @Override
    public CommonResponse updatePaymentStatus(OrderDto orderDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (orderDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Order ID is required for update."));
                return commonResponse;
            }

            Order existingOrder = orderRepo.findById(orderDto.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            existingOrder.setPaymentStatus(orderDto.getPaymentStatus());
            orderRepo.save(existingOrder);

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList("Payment status updated successfully"));

            // Send email notification
            User user = userRepo.findById(Long.valueOf(existingOrder.getUserId())).orElse(null);
            if (user != null) {
                // Adapt your email service to handle orders
                // emailService.sendOrderStatusUpdateEmail(existingOrder, user);
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in OrderService -> updatePaymentStatus()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the order payment status."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse updateOrderStatus(OrderDto orderDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (orderDto.getId() == null) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("Order ID is required for update."));
                return commonResponse;
            }

            Order existingOrder = orderRepo.findById(orderDto.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            existingOrder.setOrderStatus(orderDto.getOrderStatus());
            orderRepo.save(existingOrder);

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList("Order status updated successfully"));

            // Send email notification
            User user = userRepo.findById(Long.valueOf(existingOrder.getUserId())).orElse(null);
            if (user != null) {
                // Adapt your email service to handle orders
                // emailService.sendOrderStatusUpdateEmail(existingOrder, user);
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in OrderService -> updateOrderStatus()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the order status."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAllOrdersByUserId(String userId) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Order> orderList = orderRepo.findByUserId(userId);
            if (!orderList.isEmpty()) {
                List<OrderDto> orderDtoList = orderList.stream()
                        .map(this::castEntityToDto)
                        .collect(Collectors.toList());
                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList(orderDtoList));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("No orders found for the user."));
            }
        } catch (Exception e) {
            SchemaToolingLogging.LOGGER.error("/**************** Exception in OrderService -> getAllOrdersByUserId()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching orders for the user."));
        }
        return commonResponse;
    }

    private Order castOrderDtoToEntity(OrderDto orderDto) {
        Order order = new Order();
        // Set basic order information
        order.setCreatedAt(orderDto.getCreatedAt() != null ? orderDto.getCreatedAt() : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        order.setReceiverAddress(orderDto.getReceiverAddress());
        order.setOrderTotal(Double.valueOf(orderDto.getTotalPrice()));
        order.setZip(orderDto.getZip());
        order.setCommonStatus(CommonStatus.ACTIVE);
        order.setOrderStatus(orderDto.getOrderStatus() != null ? orderDto.getOrderStatus() : OrderStatus.PENDING);
        order.setPaymentStatus(orderDto.getPaymentStatus() != null ? orderDto.getPaymentStatus() : PaymentStatus.NOT_PAID);
        order.setUserId(orderDto.getUserId());
        return order;
    }

    private OrderDto castEntityToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setReceiverAddress(order.getReceiverAddress());
        orderDto.setTotalPrice(String.valueOf(order.getOrderTotal()));
        orderDto.setZip(order.getZip());
        orderDto.setCommonStatus(order.getCommonStatus());
        orderDto.setOrderStatus(order.getOrderStatus());
        orderDto.setPaymentStatus(order.getPaymentStatus());
        orderDto.setUserId(order.getUserId());

        // Get item quantities
        if (order.getOrderItems() != null) {
            Map<Long, Integer> itemQuantities = new HashMap<>();
            for (OrderItemQuantity oiq : order.getOrderItems()) {
                itemQuantities.put(oiq.getItem().getId(), oiq.getQuantity());
            }
            orderDto.setItemQuantities(itemQuantities);
        }
        return orderDto;
    }

    private List<String> orderValidation(OrderDto orderDto) {
        List<String> validationList = new ArrayList<>();
        if (CommonValidation.stringNullValidation(orderDto.getReceiverAddress())) {
            validationList.add("Receiver address cannot be empty");
        }
        if (CommonValidation.stringNullValidation(orderDto.getTotalPrice())) {
            validationList.add("Total price cannot be empty");
        }
        if (CommonValidation.stringNullValidation(orderDto.getUserId())) {
            validationList.add("User ID cannot be empty");
        }
        if (orderDto.getItemQuantities() == null || orderDto.getItemQuantities().isEmpty()) {
            validationList.add("Order must contain at least one item");
        }
        return validationList;
    }
}

