package com.example.product_store.store.category.exceptions;

import com.example.product_store.store.category.CategoryErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCategoryException extends RuntimeException {
  public DuplicateCategoryException() {
    super(CategoryErrorMessages.DUPLICATE_CATEGORY_EXCEPTION.getMessage());
  }
}
