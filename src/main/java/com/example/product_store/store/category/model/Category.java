package com.example.product_store.store.category.model;

import com.example.product_store.store.category.dto.CategoryRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "category_id")
  private String id;

  @Column(name = "category_name")
  private String categoryName;

  public Category(CategoryRequestDTO requestDTO){
    this.categoryName = requestDTO.getName();
  }
}
