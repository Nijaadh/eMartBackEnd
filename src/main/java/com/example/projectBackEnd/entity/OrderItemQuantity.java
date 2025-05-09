package com.example.projectBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "order_item_qty")
public class OrderItemQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties("orderItems")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties({"gifts", "subCategory"})
    private Items item;

    @Column
    private Integer quantity;
}
