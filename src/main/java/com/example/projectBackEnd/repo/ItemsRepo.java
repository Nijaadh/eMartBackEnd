package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepo extends JpaRepository<Items,Long> {
    List<Items> findByNameContainingIgnoreCase(String name);
    List<Items> findBySubCategoryAndCommonStatus(SubCategory subCategory, CommonStatus status);
}
