package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithSubCategoryDto {
    private Long id;
    private String name;
    private String description;
    private String image;
    private CommonStatus commonStatus;
    private List<SubCategoryDto> subCategories;
}
