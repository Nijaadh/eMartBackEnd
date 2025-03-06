package com.example.projectBackEnd.entity;

import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Gift")
public class Gift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String giftName;

    @Column
    private String sendingDate;

    @Column
    private String createdAt;

    @Column
    private Double totalPrice;

    @Column
    private String recieverAddress;

    @Column
    private String zip;

    @Enumerated
    private CommonStatus commonStatus;

    @ManyToMany
    @JoinTable(
            name = "gift_items",
            joinColumns = @JoinColumn(name = "gift_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Items> items;


    @Column
    private String userId;

    @Enumerated
    private PaymentStatus paymentStatus;
}
