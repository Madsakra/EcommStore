package com.example.product_store.security.errors;

public class AccountAlreadyExistsException extends RuntimeException {

  public AccountAlreadyExistsException(String message) {
    super(message);
  }
}
