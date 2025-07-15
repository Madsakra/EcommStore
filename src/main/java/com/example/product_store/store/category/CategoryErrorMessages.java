package com.example.product_store.store.category;

public enum CategoryErrorMessages {
  CATEGORY_NOT_FOUND("Unable to find category with the given id"),
  CATEGORY_NOT_VALID("Unable to create / update category due to invalid payload"),
  DUPLICATE_CATEGORY_EXCEPTION("Unable to create / update category due to duplicate category");

  private final String message;

  CategoryErrorMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
