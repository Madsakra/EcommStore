package com.example.product_store.store.product.exceptions;
import com.example.product_store.store.product.ProductErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// USED FOR CREATE / UPDATE
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException() {
    super(ProductErrorMessages.PRODUCT_NOT_FOUND.getMessage());
  }

  // Constructor with productId or any custom message
  public ProductNotFoundException(String productId) {
    super(String.format("%s: %s", ProductErrorMessages.PRODUCT_NOT_FOUND.getMessage(), productId));
  }
}
