package com.example.product_store.authentication.errors;
// THROWN BY LOGIN SERVICE
// WHEN UserDetails is invalid
public class InvalidUserDetailsException extends RuntimeException {
  public InvalidUserDetailsException(String message) {
    super(message);
  }
}
