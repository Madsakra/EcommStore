package com.example.product_store.store.product;

import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateProductCommand {

  // USED FOR UPDATING PRODUCTS
  // COMMAND WILL CONTAIN BOTH id AND RequestDTO
  private String id;
  private ProductRequestDTO requestDTO;

}
