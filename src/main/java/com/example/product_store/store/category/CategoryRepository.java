package com.example.product_store.store.category;

import com.example.product_store.store.category.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
  boolean existsByCategoryName(String categoryName);

  // SPRING DATA JPA
  List<Category> findByCategoryNameContaining(String categoryName);
}
