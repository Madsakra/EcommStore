package com.example.product_store;

public interface Command<I, O> {
  O execute(I input);
}
