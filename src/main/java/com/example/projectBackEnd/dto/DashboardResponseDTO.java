package com.example.projectBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    private SummaryDTO summary;
    private ChartsDTO charts;
    private List<LowStockItemDTO> lowStockItems;
}
