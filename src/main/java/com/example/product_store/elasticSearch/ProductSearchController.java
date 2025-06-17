package com.example.product_store.elasticSearch;

import com.example.product_store.elasticSearch.model.ProductES;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductSearchController {
    private final ProductESRepository productESRepository;

    @GetMapping("/by-price")
    public ResponseEntity<List<ProductES>> searchByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return ResponseEntity.ok(productESRepository.findByPriceBetween(minPrice, maxPrice));
    }

    @GetMapping("/by-categories")
    public ResponseEntity<List<ProductES>> searchByCategories(
            @RequestParam List<String> categoryIds) {

        List<ProductES> mylist = productESRepository.findByCategoriesIdIn(categoryIds);
        System.out.println(mylist);
        return ResponseEntity.ok(productESRepository.findByCategoriesIdIn(categoryIds));
    }

    @GetMapping("/by-price-and-categories")
    public ResponseEntity<List<ProductES>> searchByPriceAndCategories(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam List<String> categoryIds) {
        return ResponseEntity.ok(
                productESRepository.findByPriceBetweenAndCategories(minPrice, maxPrice, categoryIds));
    }
}
