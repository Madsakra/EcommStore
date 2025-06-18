package com.example.product_store.order.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "product_id")
  private String productId;

  private Integer quantity;

  @Column(name = "price_at_purchase")
  private BigDecimal priceAtPurchase;


  @ManyToOne
  @JoinColumn(name="order_id",referencedColumnName = "id")
  @ToString.Exclude
  private Order order;
}
