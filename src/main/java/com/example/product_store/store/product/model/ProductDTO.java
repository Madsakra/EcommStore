package com.example.product_store.store.product.model;

import com.example.product_store.store.category.model.CategoryDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class ProductDTO implements Serializable {

  private String id;
  private String title;
  private String description;
  private Integer stock;
  private Double price;
  private List<CategoryDTO> categories;

  public ProductDTO(Product product) {
    this.id = product.getId();
    this.title = product.getTitle();
    this.description = product.getDescription();
    this.stock = product.getStock();
    this.price = product.getPrice();
    this.categories =
        product.getCategories() != null
            ? product.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toList())
            : new ArrayList<>();
  }
}
