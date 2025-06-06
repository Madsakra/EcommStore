package com.example.product_store.store.product.exceptions;

import com.example.product_store.store.product.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException() {
    super(ErrorMessages.PRODUCT_NOT_FOUND.getMessage());
  }
}
