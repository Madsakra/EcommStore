package com.example.product_store.store.category.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class CategoryDTO implements Serializable {

  private String id;
  private String categoryName;

  public CategoryDTO(Category category) {
    this.id = category.getId();
    this.categoryName = category.getCategoryName();
  }
}
