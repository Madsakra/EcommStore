package com.example.product_store.authentication.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // FILTER EVERY INCOMING REQUEST
  // CHECK IF JWT IS VALID

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;
    final String jti;

    // 1. Check for auth header and bearer prefix
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);

    // 2. check if token still valid
    if (JwtUtil.isTokenValid(jwt)) {
      Claims claims = JwtUtil.getClaims(jwt);
      username = claims.getSubject();

      jti = claims.getId();
      // 3. CRITICAL: CHECK IF USER IS NOT ALREADY AUTHENTICATED
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // 4. Get the authorities from the token's claims
        List<String> authoritiesList = claims.get("authorities", List.class);
        List<SimpleGrantedAuthority> grantedAuthorities =
            authoritiesList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // 5. Create the Authentication object
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                jti, // store user uuid as principal
                null, // No credentials needed as we are using JWT
                grantedAuthorities // The user's authorities
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 6. Set the Authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
