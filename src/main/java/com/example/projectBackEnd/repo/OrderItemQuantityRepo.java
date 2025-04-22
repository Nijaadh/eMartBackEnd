package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.entity.OrderItemQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemQuantityRepo extends JpaRepository<OrderItemQuantity, Long> {
}