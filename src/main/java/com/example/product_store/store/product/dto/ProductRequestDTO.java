package com.example.product_store.store.product.dto;

import com.example.product_store.store.category.dto.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    // WHEN CLIENT WANTS TO CREATE, UPDATE , DELETE
    // USE THIS CLASS TO HOLD THEIR PAYLOAD INSTEAD
    private String title;
    private String description;
    private Integer stock;
    private BigDecimal price;
    private List<CategoryDTO> categories;
    private String createdBy;
}
