package com.example.product_store.store.product.exceptions;

import com.example.product_store.store.product.ProductErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// WHEN ANOTHER ADMIN TRY TO MANIPULATE ANOTHER ADMINS' PRODUCTS
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedManagement extends RuntimeException {
  public UnauthorizedManagement() {
    super(ProductErrorMessages.UNAUTHORIZED_MANAGEMENT.getMessage());
  }
}
