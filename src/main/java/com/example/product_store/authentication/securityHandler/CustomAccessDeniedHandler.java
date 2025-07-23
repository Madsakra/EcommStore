package com.example.product_store.authentication.securityHandler;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;


public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  // WHEN CLIENT TRIES TO ACCESS OTHER ROLE RESOURCES
  // WILL DISPLAY RESPONSE AND BLOCK THEM OUT
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

      // BUILD A RESPONSE ENTITY
      ResponseEntity<ErrorResponse> entity = ErrorResponseTemplate.buildResponseError(
              "Access Denied",
              "You do not have the required permission to access this resource.",
              HttpStatus.UNAUTHORIZED
      );

      response.setStatus(401);
      response.setContentType("application/json");
      response.getWriter().write(new ObjectMapper().writeValueAsString(entity.getBody()));

  }
}
