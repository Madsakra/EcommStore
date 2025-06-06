package com.example.product_store.store.product;

import com.example.product_store.store.product.model.Product;
import lombok.Getter;

@Getter
public class UpdateProductCommand {

  private String id;
  private Product product;

  public UpdateProductCommand(String id, Product product) {
    this.product = product;
    this.id = id;
  }
}
