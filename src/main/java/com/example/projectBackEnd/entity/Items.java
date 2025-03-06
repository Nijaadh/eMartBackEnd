package com.example.projectBackEnd.entity;

import com.example.projectBackEnd.constant.CommonStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Column( columnDefinition = "LONGTEXT")
    private String image;

    @Column
    private CommonStatus commonStatus;

    @ManyToMany(mappedBy = "items")
    private Set<Gift> gifts;
}
