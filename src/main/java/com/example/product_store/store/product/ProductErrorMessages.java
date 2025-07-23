package com.example.product_store.store.product;

import lombok.Getter;

// HELPS TO CENTRALISE EXCEPTION MESSAGES IN 1 PLACE
// EXCEPTION THAT SHARE THE SAME MESSAGE
@Getter
public enum ProductErrorMessages {
  PRODUCT_NOT_FOUND("Product not found based on given id."),
  UNAUTHORIZED_MANAGEMENT("Not authorized to access the following endpoint resources.");

  private final String message;

  ProductErrorMessages(String message) {
    this.message = message;
  }
}
