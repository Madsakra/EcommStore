package com.example.product_store.authentication.errors;

// USED IN ACCOUNT VALIDATOR (ACCOUNT CREATION)
public class AccountNotValidException extends RuntimeException {
  public AccountNotValidException(String message) {
    super(message);
  }
}
