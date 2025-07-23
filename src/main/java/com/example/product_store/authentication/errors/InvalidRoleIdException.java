package com.example.product_store.authentication.errors;

// USED IN ACCOUNT VALIDATION (ACCOUNT CREATION)
// INVALID ROLES WILL NOT ALLOW FOR ACCOUNT CREATION
public class InvalidRoleIdException extends RuntimeException {
  public InvalidRoleIdException(String message) {
    super(message);
  }
}
