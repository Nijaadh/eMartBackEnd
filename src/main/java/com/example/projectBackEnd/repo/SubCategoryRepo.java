package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.entity.Category;
import com.example.projectBackEnd.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepo extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategoryAndCommonStatus(Category category, CommonStatus status);

    List<SubCategory> findByNameContainingIgnoreCase(String name);

    List<SubCategory> findByCategoryId(Long id);
}
