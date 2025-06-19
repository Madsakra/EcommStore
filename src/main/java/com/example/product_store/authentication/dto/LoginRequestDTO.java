package com.example.product_store.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequestDTO {

  private String identifier;
  private String password;
}
