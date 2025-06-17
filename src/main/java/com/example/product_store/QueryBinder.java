package com.example.product_store;

public interface QueryBinder<I, O> {
  O execute(I input);
}
