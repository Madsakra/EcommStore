package com.example.product_store.elasticSearch;

import com.example.product_store.elasticSearch.model.ProductES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductESRepository extends ElasticsearchRepository<ProductES,String> {
    // Filter by price range
    List<ProductES> findByPriceBetween(Double minPrice, Double maxPrice);

    // Filter by category IDs
    List<ProductES> findByCategoriesIdIn(Collection<String> categoryIds);

    // Filter by both price range and categories
    @Query("{\"bool\": {\"must\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}, {\"terms\": {\"categories.id\": ?2}}]}}")
    List<ProductES> findByPriceBetweenAndCategories(Double minPrice, Double maxPrice, Collection<String> categoryIds);
}
