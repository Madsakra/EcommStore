package com.example.product_store.store.category.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="category_id")
    private String id;

    @Column(name="category_name")
    private String categoryName;


}
