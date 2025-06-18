package com.example.product_store.order.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;


  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "total_price")
  private BigDecimal totalPrice;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany
  private List<OrderItem> items = new ArrayList<>();


}
