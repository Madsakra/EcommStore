package com.example.product_store.authentication.service;

import com.example.product_store.authentication.dto.CachedUserDetailsDTO;
import com.example.product_store.authentication.jwt.MyUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginUserDetailsService implements UserDetailsService {

  // MEANT TO CHECK IF THE USERNAME / EMAIL EXIST IN DB FIRST
  // TRIGGERED WHEN LOGGING IN THROUGH AUTHENTICATION MANAGER
  private final LoadCachedUserService loadCachedUserDetails;

  private static final Logger logger = LoggerFactory.getLogger(LoginUserDetailsService.class);

  public LoginUserDetailsService(LoadCachedUserService loadCachedUserDetails) {
    this.loadCachedUserDetails = loadCachedUserDetails;
  }

  // can log in by email
  // can log in by username
  @Override
  public MyUserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
    CachedUserDetailsDTO cachedUser = loadCachedUserDetails.execute(loginIdentifier);
    List<GrantedAuthority> authorities =
        cachedUser.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    return new MyUserDetails(cachedUser.getId(), cachedUser.getUsername(), cachedUser.getHashedPassword(), authorities);
  }
}
