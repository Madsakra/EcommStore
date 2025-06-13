package com.example.product_store.store.product.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedManagement extends RuntimeException {
  public UnauthorizedManagement(String message) {
    super(message);
  }
}
