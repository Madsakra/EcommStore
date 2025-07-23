package com.example.product_store.authentication.securityHandler;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  // BLOCKS OUT USER THAT ARE UNAUTHENTICATED
  // SEND THEM RESPONSE ENTITY WITH 401 STATUS
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
    response.setContentType("application/json");
    ErrorResponse error = ErrorResponseTemplate.buildError(
            "No bearer token","Unable to detect bearer token in authorization header.", HttpStatus.UNAUTHORIZED
    );

    new ObjectMapper().writeValue(response.getWriter(), error);
  }
}
