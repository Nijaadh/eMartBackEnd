package com.example.projectBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItemDTO {
    private Long id;
    private String name;
    private String category;
    private int currentStock;
    private int reorderLevel;
}
