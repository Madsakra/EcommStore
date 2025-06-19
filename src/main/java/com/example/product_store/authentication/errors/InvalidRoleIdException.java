package com.example.product_store.authentication.errors;

public class InvalidRoleIdException extends RuntimeException {
  public InvalidRoleIdException(String message) {
    super(message);
  }
}
