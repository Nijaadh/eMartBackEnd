package com.example.projectBackEnd.entity;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Items")
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private Double unitPrice;

    @Column
    private String Description;

    @Column
    private String Category;

    @Column(columnDefinition = "LONGTEXT")
    private String image;

    @Column
    private CommonStatus commonStatus;

    // New columns
    @Column
    private Integer itemCount;

    @Column
    private Integer salesCount;

    @Column
    private Double discount;

    @Column
    private Integer reOrderLevel;

    // Added createdAt field
    @Column
    private Timestamp createdAt;

    @ManyToMany(mappedBy = "items")
    private Set<Gift> gifts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;
}
