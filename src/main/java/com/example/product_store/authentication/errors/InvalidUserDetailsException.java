package com.example.product_store.authentication.errors;

public class InvalidUserDetailsException extends RuntimeException {
  public InvalidUserDetailsException(String message) {
    super(message);
  }
}
