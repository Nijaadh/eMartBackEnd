package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);

    // Note: createdAt is a String in your entity, so we need to adjust the query
    @Query(value = "SELECT COUNT(*) FROM Orders WHERE DATE(created_at) = :date", nativeQuery = true)
    int countOrdersByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(order_total), 0) FROM Orders WHERE DATE(created_at) = :date", nativeQuery = true)
    double sumRevenueByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(order_total), 0) FROM Orders WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    double sumRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT order_status, COUNT(*) FROM Orders GROUP BY order_status", nativeQuery = true)
    List<Object[]> countOrdersByStatus();
}
