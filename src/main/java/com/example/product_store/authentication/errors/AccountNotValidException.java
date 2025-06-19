package com.example.product_store.authentication.errors;

public class AccountNotValidException extends RuntimeException {
  public AccountNotValidException(String message) {
    super(message);
  }
}
