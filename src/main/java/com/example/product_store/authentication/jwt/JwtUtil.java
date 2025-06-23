package com.example.product_store.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

public class JwtUtil {

  // SECRET KEY - to be thrown to ENV
  public static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
  private static final String secretKey = System.getenv("JWT_SECRET");
  // Duration of jwt token: 15 minutes
  private static final Duration expiration = Duration.ofMinutes(15);

  public static String generateToken(MyUserDetails myUserDetails) {

    List<String> roles =
        myUserDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return Jwts.builder()
        .claim("authorities", roles)
        .subject(myUserDetails.getUsername())
        .issuedAt(new Date())
        .expiration(Date.from(Instant.now().plus(expiration)))
        .signWith(getSigningKey())
        .id(myUserDetails.getId())
        .compact();
  }

  public static Claims getClaims(String token) {
    // This is the correct way to parse a SIGNED token (JWS)
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public static boolean isTokenValid(String token) {
    try {
      // We wrap this in a try-catch because an expired token will throw an ExpiredJwtException
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      // This includes ExpiredJwtException, MalformedJwtException, etc.
      return false;
    }
  }

  private static SecretKey getSigningKey() {
    byte[] keybytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keybytes);
  }

  public static String getDuration(){
    return  String.valueOf(expiration.toMinutes());
  }
}
