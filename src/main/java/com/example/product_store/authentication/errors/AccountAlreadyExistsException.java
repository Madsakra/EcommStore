package com.example.product_store.authentication.errors;

public class AccountAlreadyExistsException extends RuntimeException {

  public AccountAlreadyExistsException(String message) {
    super(message);
  }
}
