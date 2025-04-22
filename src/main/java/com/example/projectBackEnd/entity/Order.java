package com.example.projectBackEnd.entity;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.OrderStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Orders") // Using "Orders" instead of "Order" as "Order" is a reserved keyword in SQL
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String createdAt;

    @Column
    private String receiverAddress;

    @Column
    private Double orderTotal;

    @Column
    private String zip;

    @Enumerated(EnumType.STRING)
    private CommonStatus commonStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column
    private String userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemQuantity> orderItems;
}
