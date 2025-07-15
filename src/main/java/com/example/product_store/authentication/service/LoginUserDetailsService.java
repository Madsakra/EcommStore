package com.example.product_store.authentication.service;

import com.example.product_store.authentication.dto.CachedUserDetailsDTO;
import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.jwt.MyUserDetails;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    // MEANT TO CHECK IF THE USERNAME / EMAIL EXIST IN DB FIRST
    // TRIGGERED WHEN LOGGING IN THROUGH AUTHENTICATION MANAGER
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginUserDetailsService.class);

    public LoginUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Cacheable(value = "userDetailsCache", key = "#loginIdentifier")
    public MyUserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        CachedUserDetailsDTO cachedUser = loadCachedUserDetails(loginIdentifier);

        List<GrantedAuthority> authorities = cachedUser.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new MyUserDetails(
                cachedUser.getId(),
                cachedUser.getUsername(),
                cachedUser.getPassword(),
                authorities
        );
    }

    // FETCH THE ACCOUNT BACK
    // FIRST FROM CACHE IF AVAILABLE
    @Cacheable(value = "userDetailsCache", key = "#loginIdentifier")
    public CachedUserDetailsDTO loadCachedUserDetails(String loginIdentifier) {
        Optional<Account> optionalAccount =
                accountRepository.findUserByEmailOrUserName(loginIdentifier);

        if (optionalAccount.isEmpty()) {
            logger.warn("LoginService: Account with identifier: {} not found", loginIdentifier);
            throw new AccountNotFoundException("Account does not exist!");
        }

        Account account = optionalAccount.get();

        Set<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        return new CachedUserDetailsDTO(account.getId(),account.getUserName(), account.getPassword(), roles);
    }

}
