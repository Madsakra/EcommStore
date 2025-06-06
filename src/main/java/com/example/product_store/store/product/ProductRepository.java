package com.example.product_store.store.product;

import com.example.product_store.store.product.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

  boolean existsByTitleAndPrice(String title, Double price);

  // SPRING DATA JPA
  // SEARCH BY TITLE
  List<Product> findByTitleContaining(String title);

  // SEARCH BY Description
  List<Product> findByDescriptionContaining(String description);
}
