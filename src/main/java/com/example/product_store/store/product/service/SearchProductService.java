package com.example.product_store.store.product.service;

import com.example.product_store.Query;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.ProductDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductService implements Query<String, List<ProductDTO>> {


    private final ProductRepository productRepository;

    public SearchProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // DEFAULT SEARCH BY TITLE
    // METHOD SIGNATURE -> execute
    @Override
//    @Cacheable(cacheNames = "product", key = "#title")
    public List<ProductDTO> execute(String title){
        return productRepository.findByTitleContaining(title)
                .stream()
                .map(ProductDTO::new)
                .toList();

    }

    // SEARCH BY DESCRIPTION
//    @Cacheable(cacheNames = "product", key = "#description")
    public List<ProductDTO> searchProductByDescription(String description){
        return productRepository.findByDescriptionContaining(description)
                .stream()
                .map(ProductDTO::new)
                .toList();

    }

}
