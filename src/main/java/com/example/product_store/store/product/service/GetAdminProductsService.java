package com.example.product_store.store.product.service;


import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetAdminProductsService {
    private final ProductRepository productRepository;

    public GetAdminProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ALLOW THE ADMIN TO VIEW OWN PRODUCTS
    public Page<ProductDTO> execute(String jti, Pageable pageable){
        Page<ProductDTO> productDTOS = productRepository.findAllByCreatedBy(jti,pageable).map(ProductDTO::new);
        return productDTOS;
    }

}
