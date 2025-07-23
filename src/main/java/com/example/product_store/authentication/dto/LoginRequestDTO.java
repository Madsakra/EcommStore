package com.example.product_store.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {
  // PAYLOAD FOR LOGGING IN
  private String identifier;
  private String password;
}
