package com.example.product_store.security.controller;

import com.example.product_store.security.jwt.JwtUtil;
import com.example.product_store.security.model.LoginRequest;
import com.example.product_store.security.model.LoginResponse;
import com.example.product_store.security.model.MyUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  private final AuthenticationManager manager;

  public LoginController(AuthenticationManager manager) {
    this.manager = manager;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getIdentifier(), loginRequest.getPassword());
    // this will fault if credentials not valid
    Authentication authentication = manager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwtToken = JwtUtil.generateToken((MyUserDetails) authentication.getPrincipal());
    return ResponseEntity.ok(new LoginResponse(jwtToken));
  }
}
