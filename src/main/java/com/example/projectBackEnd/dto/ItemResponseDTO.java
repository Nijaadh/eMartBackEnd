package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.Data;

@Data
public class ItemResponseDTO {
    private Long id;
    private String name;
    private Double unitPrice;
    private String Description;
    private String Category;
    private String image;
    private CommonStatus commonStatus;
    private Integer itemCount;
    private Integer salesCount;
    private Double discount;
    private Integer reOrderLevel;
    private String subCategoryName;
}
