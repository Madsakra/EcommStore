package com.example.product_store.store.product.service;

import com.example.product_store.Command;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteProductService implements Command<String,Void> {

    private final ProductRepository productRepository;


    public DeleteProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "product",allEntries = true),
                    @CacheEvict(cacheNames = "getAllProducts",key = "'allProducts'")

            }
    )
    public Void execute(String id){
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()){
            productRepository.deleteById(id);
            return null;
        }

        throw new ProductNotFoundException();
    }
}
