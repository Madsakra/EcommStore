package com.example.product_store.authentication.dto;

import com.example.product_store.authentication.jwt.JwtUtil;
import lombok.Data;

import java.time.Duration;

@Data
public class LoginResponseDTO {
  // TO BE RETURNED FROM LOGIN DTO
  private String token;
  private String type = "bearer";
  private String expiresIn;

  public LoginResponseDTO(String token){
    this.token = token;
    this.expiresIn = JwtUtil.getDuration() + " Minutes";
  }

}
