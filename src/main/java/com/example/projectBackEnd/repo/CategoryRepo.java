package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);
}
