package com.example.product_store.authentication.errors;

// EXCEPTION USED IN LoadCachedUser
// Exception USED IN Get / Adding / deleting favourites services
public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(String message) {
    super(message);
  }
}
