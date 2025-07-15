package com.example.product_store.store.product;

import lombok.Getter;

// HELPS TO CENTRALISE ERROR MESSAGES IN 1 PLACE
// ONLY EXCEPTION NOT INCLUDED IS
// INVALID PAGE REQUEST , PRODUCT NOT VALID (DUE TO HOW DYNAMIC IT CAN BE)
@Getter
public enum ProductErrorMessages {
  PRODUCT_NOT_FOUND("Product not found based on given id."),
  UNAUTHORIZED_MANAGEMENT("Not authorized to access the following endpoint resources.");

  private final String message;

  ProductErrorMessages(String message) {
    this.message = message;
  }
}
