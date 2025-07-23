package com.example.product_store.authentication.errors;

// USED IN ACCOUNT CREATION (ACCOUNT VALIDATOR)
// WHEN ROLES DOESN'T MATCH
public class RoleMismatchException extends RuntimeException {
  public RoleMismatchException(String message) {
    super(message);
  }
}
