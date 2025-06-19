package com.example.product_store.authentication.model;

import com.example.product_store.store.product.model.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  // WILL NEED A CUSTOM CONSTRUCTOR TO SET THE favouriteProducts as null
  public Account(String id, String userName, String email, String password, Set<Role> roles) {
    this.id = id;
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.roles = roles;
    this.favouriteProducts = new HashSet<>();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private String id;

  @Column(name = "username")
  private String userName;

  private String email;

  @Column(name = "password_hash")
  private String password;

  @ManyToMany
  @JoinTable(
      name = "account_roles",
      joinColumns = @JoinColumn(name = "account_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  @ManyToMany
  @JoinTable(
          name = "user_favorite",
          joinColumns = @JoinColumn(name = "account_id"),
          inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  private Set<Product> favouriteProducts = new HashSet<>();
  private BigDecimal balance;
}
