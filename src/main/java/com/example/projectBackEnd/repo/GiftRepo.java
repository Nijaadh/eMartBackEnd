package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import com.example.projectBackEnd.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GiftRepo extends JpaRepository<Gift,Long> {
    List<Gift> findByUserId(String userId);

    long countByCommonStatus(CommonStatus commonStatus);

    @Query("SELECT SUM(g.totalPrice) FROM Gift g WHERE g.paymentStatus = :status")
    Double getTotalPriceByPaymentStatus(PaymentStatus status);

    @Query("SELECT MONTH(STR_TO_DATE(g.createdAt, '%Y-%m-%d')), SUM(g.totalPrice) " +
            "FROM Gift g " +
            "WHERE YEAR(STR_TO_DATE(g.createdAt, '%Y-%m-%d')) = YEAR(CURDATE()) " +
            "GROUP BY MONTH(STR_TO_DATE(g.createdAt, '%Y-%m-%d'))")
    List<Object[]> findMonthlyIncomeForCurrentYear();
}
