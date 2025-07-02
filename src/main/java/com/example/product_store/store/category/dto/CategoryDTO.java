package com.example.product_store.store.category.dto;

import java.io.Serializable;

import com.example.product_store.store.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO implements Serializable {

  private String id;
  private String categoryName;

  public CategoryDTO(Category category) {
    this.id = category.getId();
    this.categoryName = category.getCategoryName();
  }
}
