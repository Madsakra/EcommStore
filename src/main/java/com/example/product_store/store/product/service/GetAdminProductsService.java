package com.example.product_store.store.product.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.repositories.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetAdminProductsService {
    private final ProductRepository productRepository;

    public GetAdminProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ALLOW THE ADMIN TO VIEW OWN PRODUCTS
    public List<ProductDTO> execute(String jti, Pageable pageable){
        List<ProductDTO> productDTOS = productRepository.findAllByCreatedBy(jti,pageable).stream().map(ProductDTO::new).toList();
        return productDTOS;
    }

}
