package com.example.product_store.authentication.errors;

public class AccountAlreadyExistsException extends RuntimeException {

  // ACCOUNT CREATION EXCEPTION
  public AccountAlreadyExistsException(String message) {
    super(message);
  }
}
