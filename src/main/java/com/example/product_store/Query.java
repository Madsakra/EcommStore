package com.example.product_store;

public interface Query<I, O> {
  O execute(I input);
}
