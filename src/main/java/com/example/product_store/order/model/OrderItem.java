package com.example.product_store.order.model;


import com.example.product_store.store.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

  // auto generated id
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "order_item_id")
  private String orderItemId;

  @Column(name = "product_id")
  private String productId;

  private Integer quantity;

  @Column(name = "price_at_purchase")
  private BigDecimal priceAtPurchase;



  @ManyToOne
  @JoinColumn(name="order_id",referencedColumnName = "order_id")
  @ToString.Exclude
  private Order order;

  public OrderItem(Product product,Integer quantity,Order order){
    this.productId = product.getId();
    this.quantity = quantity;
    this.priceAtPurchase = product.getPrice();
    this.order = order;
  }

}
