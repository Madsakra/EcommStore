package com.example.product_store;

import org.springframework.http.ResponseEntity;

public interface Query<I,O> {
    O execute(I input);
}
