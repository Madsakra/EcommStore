package com.example.product_store.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "role_id", columnDefinition = "CHAR(36)")
  private String roleId;

  @Column(name = "role_name", length = 50, unique = true, nullable = false)
  private String roleName;
}
