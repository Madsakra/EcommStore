package com.example.product_store.store.product.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductFilterRequest {
    List<String> categoryIds;
    Double minPrice;
    Double maxPrice;
    Integer page;
    Integer size;
    String sortField; // e.g. "title"
    String sortOrder; // "asc" or "desc"
}
