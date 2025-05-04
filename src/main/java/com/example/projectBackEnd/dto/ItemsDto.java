package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsDto {
    private String id;
    private String name;
    private String unitPrice;
    private String Description;
    private String Category;
    private String image;
    private CommonStatus commonStatus;
    private Long subCategoryId;
    private Integer itemCount;
    private Integer salesCount;
    private String discount;
    private Integer reOrderLevel;
    private Timestamp createdAt;
}
