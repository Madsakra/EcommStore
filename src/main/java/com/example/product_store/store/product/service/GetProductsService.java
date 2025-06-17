package com.example.product_store.store.product.service;



import com.example.product_store.QueryBinder;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.dto.ProductDTO;


import java.util.List;



import org.springframework.data.domain.Page;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;


import org.springframework.stereotype.Service;

@Service
public class GetProductsService implements QueryBinder<Void, List<ProductDTO>> {

    private final ProductRepository productRepository;

    public GetProductsService(ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    // ORDINARY SEARCH
    // TO BE REPLACED BY ELASTIC SEARCH ONCE WORKING
    @Override
    @Cacheable(cacheNames = "getAllProducts", key = "'allProducts'")
    public List<ProductDTO> execute(Void input) {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
        return productDTOS;
    }

    public Page<ProductDTO> getPagedProducts(int page, int size) {
        if (size <= 0) {
            throw new InvalidPageRequestException("Page size must be more than 0");
        }

        Page<Product> pagedResult = productRepository.findAll(PageRequest.of(page, size));
        return pagedResult.map(ProductDTO::new);
    }


}
