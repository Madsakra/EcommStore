package com.example.product_store.elasticSearch.service;

import com.example.product_store.elasticSearch.ProductESRepository;
import com.example.product_store.elasticSearch.model.CategoryES;
import com.example.product_store.elasticSearch.model.ProductES;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductESRepository productESRepository;

    @Transactional
    public void syncProductsToES() {
        List<Product> products = productRepository.findAll();
        List<ProductES> productESList = products.stream()
                .map(this::convertToES)
                .collect(Collectors.toList());
        productESRepository.saveAll(productESList);
    }

    private ProductES convertToES(Product product) {
        ProductES productES = new ProductES();
        productES.setId(product.getId());
        productES.setTitle(product.getTitle());
        productES.setDescription(product.getDescription());
        productES.setStock(product.getStock());
        productES.setPrice(product.getPrice());
        productES.setCreatedBy(product.getCreatedBy());

        List<CategoryES> categoryESList = product.getCategories().stream()
                .map(c -> {
                    CategoryES categoryES = new CategoryES();
                    categoryES.setId(c.getId());
                    categoryES.setCategoryName(c.getCategoryName());
                    return categoryES;
                })
                .collect(Collectors.toList());

        productES.setCategories(categoryESList);
        return productES;
    }

}
