package com.example.product_store.store.product.repositories;

import com.example.product_store.store.product.model.Product;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

  boolean existsByTitleAndPrice(String title, BigDecimal price);

  // for processing orders
  // prevent overselling
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Product> findAllById(Iterable<String> ids);

  // FIND ALL PRODUCTS THAT ARE CREATED BY THE CURRENT ADMIN
  Page<Product> findAllByCreatedBy(String createdBy, Pageable pageable);
}
