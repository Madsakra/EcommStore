package com.example.product_store.store.category;

import com.example.product_store.store.category.model.Category;
import lombok.Getter;

@Getter
public class UpdateCategoryCommand {

  private String id;
  private Category category;

  public UpdateCategoryCommand(String id, Category category) {
    this.category = category;
    this.id = id;
  }
}
