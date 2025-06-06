package com.example.product_store.security.errors;

public class AccountNotValidException extends RuntimeException {
  public AccountNotValidException(String message) {
    super(message);
  }
}
