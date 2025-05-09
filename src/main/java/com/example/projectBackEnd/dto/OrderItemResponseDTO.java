package com.example.projectBackEnd.dto;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private Integer quantity;
    private ItemResponseDTO item;
}
