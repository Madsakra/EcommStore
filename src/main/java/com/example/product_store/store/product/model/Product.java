package com.example.product_store.store.product.model;

import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  public Product(ProductRequestDTO productRequestDTO){
    this.title = productRequestDTO.getTitle();
    this.description = productRequestDTO.getDescription();
    this.stock = productRequestDTO.getStock();
    this.price = productRequestDTO.getPrice();
    this.createdBy = productRequestDTO.getCreatedBy();
    // Map CategoryDTOs to Category entities
    if (productRequestDTO.getCategories() != null) {
      this.categories = productRequestDTO.getCategories()
              .stream()
              .map(dto -> new Category(dto.getId(), dto.getCategoryName()))
              .collect(Collectors.toList());
    } else {
      this.categories = new ArrayList<>();
    }

  }
}
