package com.example.product_store.store.product;

import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import lombok.Getter;

@Getter
public class UpdateProductCommand {

  private String id;
  private ProductRequestDTO requestDTO;

  public UpdateProductCommand(String id, ProductRequestDTO requestDTO) {
    this.requestDTO = requestDTO;
    this.id = id;
  }
}
