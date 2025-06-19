package com.example.product_store.authentication.errors;

public class RoleMismatchException extends RuntimeException {
  public RoleMismatchException(String message) {
    super(message);
  }
}
