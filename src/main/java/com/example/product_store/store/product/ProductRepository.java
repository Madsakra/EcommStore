package com.example.product_store.store.product;

import com.example.product_store.store.product.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

  boolean existsByTitleAndPrice(String title, BigDecimal price);

  // SPRING DATA JPA
  // SEARCH BY TITLE
  List<Product> findByTitleContaining(String title);

  // SEARCH BY Description
  List<Product> findByDescriptionContaining(String description);


  // for processing orders
  // prevent overselling
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Product p WHERE p.id IN :ids")
  List<Product> findAllByIdForUpdate(@Param("ids") List<String> ids);


}
