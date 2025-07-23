package com.example.product_store.authentication.jwt;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;
    final String jti;

    try {
      // 1. No Authorization header → just continue without authentication
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      jwt = authHeader.substring(7);
      // 2. Validate token (let isTokenValid throw if invalid)
      if (!JwtUtil.isTokenValid(jwt)) {
        throw new JwtException("Invalid or expired JWT");
      }

      // 3. Parse claims
      Claims claims = JwtUtil.getClaims(jwt);
      username = claims.getSubject();
      jti = claims.getId();

      // ensure that:
      // 1. claims has a username
      // 2. Security context is null (so we can set auth info to it later)
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // Get hold of the roles from claims
        List<String> authoritiesList = claims.get("authorities", List.class);

        // convert roles to authorities
        List<SimpleGrantedAuthority> grantedAuthorities =
            authoritiesList.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                jti, // UUID as principal
                null,
                grantedAuthorities);

        // include metadata about jwt
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // set to auth details (id, role) to security context
        // use it in controller for user validation
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
      filterChain.doFilter(request, response);

    } catch (JwtException ex) {
      // This includes ExpiredJwtException, MalformedJwtException, etc.
      // GROUPED THEM UNDER ONE EXCEPTION
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      ErrorResponse error =
          ErrorResponseTemplate.buildError("JWT exception encountered", ex.getMessage(), HttpStatus.UNAUTHORIZED);

      new ObjectMapper().writeValue(response.getWriter(), error);
    }
  }
}
