package com.example.product_store.store.product.dto;

import com.example.product_store.store.category.dto.CategoryDTO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.product_store.store.product.model.Product;
import lombok.Data;

@Data
public class ProductDTO implements Serializable {

  private String id;
  private String title;
  private String description;
  private Integer stock;
  private BigDecimal price;
  private List<CategoryDTO> categories;
  private String createdBy;


  public ProductDTO(Product product) {
    this.id = product.getId();
    this.title = product.getTitle();
    this.description = product.getDescription();
    this.stock = product.getStock();
    this.price = product.getPrice();
    this.createdBy = product.getCreatedBy();
    this.categories =
        product.getCategories() != null
            ? product.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toList())
            : new ArrayList<>();
  }
}
