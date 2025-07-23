package com.example.product_store.authentication.jwt;

import io.github.cdimascio.dotenv.Dotenv;
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

  public static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

  static Dotenv dotenv = Dotenv.load();
  // GET HOLD OF THE SECRET KEY IN ENV
  private static final String secretKey = dotenv.get("JWT_SECRET");
  // Duration of jwt token: 15 minutes
  private static final Duration expiration = Duration.ofMinutes(15);

  // GENERATE TOKEN USING UserDetails
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
            // JWT KEY HAS THE SIGNATURE OF THE SECRET KEY
            // MAKES IT IMPOSSIBLE TO FORGE JWT
        .signWith(getSigningKey())
        .id(myUserDetails.getId())
        .compact();
  }

  // USED IN JwtAuthenticationFilter
  // Parse the JWT to get the claims
  public static Claims getClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  // CHECK IF THE TOKEN IS VALID
  public static boolean isTokenValid(String token) {
    try {
      // We wrap this in a try-catch because an expired token will throw an ExpiredJwtException
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      // This includes ExpiredJwtException, MalformedJwtException, etc.
      // not handling the exception here, but in authentication filter
      return false;
    }
  }


  // 1. DECODE BASE 64 STRING TO BYTES
  // 2. WRAPS IT INTO A SecretKey OBJECT
  // 3. RETURN SECRET KEY TO VERIFY JWT SECURELY WITH HMAC
  private static SecretKey getSigningKey() {
    byte[] keybytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keybytes);
  }

  public static String getDuration(){
    return  String.valueOf(expiration.toMinutes());
  }
}
