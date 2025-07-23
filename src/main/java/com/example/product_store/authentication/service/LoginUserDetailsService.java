package com.example.product_store.authentication.service;

import com.example.product_store.authentication.dto.UserDetailsDTO;
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

  // loginIdentifier can be both email / username
  @Override
  public MyUserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
    // Calls loadCachedUserDetails to either get hold of existing cache or UserDetails in mysql
    // will return a UserDetailsDTO
    UserDetailsDTO userDetailsDTO = loadCachedUserDetails.execute(loginIdentifier);

    // CONVERT THE ROLES INTO A LIST OF GRANTED AUTHORITIES
    List<GrantedAuthority> authorities =
        userDetailsDTO.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    // return a UserDetails Object through instantiation of MyUserDetails
    return new MyUserDetails(userDetailsDTO.getId(), userDetailsDTO.getUsername(), userDetailsDTO.getHashedPassword(), authorities);
  }
}
