package com.example.product_store.store.product.model;

import com.example.product_store.store.category.model.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private String id;

  private String title;
  private String description;
  private Integer stock;
  private BigDecimal price;

  @Column(name="created_by")
  private String createdBy;

  @ManyToMany
  @JoinTable(
      name = "product_category",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private List<Category> categories;
}
