package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
