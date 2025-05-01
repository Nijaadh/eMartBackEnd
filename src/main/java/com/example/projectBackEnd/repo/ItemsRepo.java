package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepo extends JpaRepository<Items, Long> {
    List<Items> findByNameContainingIgnoreCase(String name);

    List<Items> findBySubCategoryAndCommonStatus(SubCategory subCategory, CommonStatus status);

    @Query(value = "SELECT COUNT(*) FROM Items", nativeQuery = true)
    int countAllItems();

    @Query(value = "SELECT COUNT(*) FROM Items WHERE item_count > 0", nativeQuery = true)
    int countItemsInStock();

    // Removed the countItemsCreatedBefore query as requested

    @Query(value = "SELECT i.id, i.name, i.category, i.item_count, i.re_order_level " +
            "FROM Items i WHERE i.item_count <= i.re_order_level " +
            "ORDER BY i.item_count ASC", nativeQuery = true)
    List<Object[]> findItemsBelowReorderLevel();
}