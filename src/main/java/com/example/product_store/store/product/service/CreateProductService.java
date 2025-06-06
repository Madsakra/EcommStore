package com.example.product_store.store.product.service;

import com.example.product_store.Command;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductDTO;
import com.example.product_store.store.product.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CreateProductService implements Command<Product, ProductDTO> {

    private final ProductRepository productRepository;
    private final ProductValidator  productValidator;


    public CreateProductService(ProductRepository productRepository,
                                ProductValidator productValidator) {
        this.productRepository = productRepository;
        this.productValidator = productValidator;
    }

    @Override
    @CacheEvict(cacheNames = "getAllProducts",key = "'allProducts'")
    public ProductDTO execute(Product product){

        productValidator.execute(product,false);
        Product savedProduct = productRepository.save(product);

        return new ProductDTO(savedProduct);

    }


}
