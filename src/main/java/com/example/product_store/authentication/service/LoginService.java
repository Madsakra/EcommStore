package com.example.product_store.authentication.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.dto.LoginRequestDTO;
import com.example.product_store.authentication.errors.InvalidUserDetailsException;
import com.example.product_store.authentication.jwt.JwtUtil;
import com.example.product_store.authentication.jwt.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements Command<LoginRequestDTO,String> {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

  private final AuthenticationManager manager;
    public LoginService(AuthenticationManager manager) {
        this.manager = manager;
    }

    @Override
    public String execute(LoginRequestDTO loginRequestDTO){
        checkRequestDTO(loginRequestDTO);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getIdentifier(), loginRequestDTO.getPassword());

        // this will fail if credentials is invalid
        Authentication authentication = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = JwtUtil.generateToken((MyUserDetails) authentication.getPrincipal());
        logger.info("LoginService: JWT successfully generated");

        return jwtToken;
    }

    // CHECK IF PAYLOAD CONSISTS OF NULL / EMPTY
    private void checkRequestDTO(LoginRequestDTO loginRequestDTO) {
        // USERNAME NULL
        if (loginRequestDTO.getIdentifier()==null){
            logger.warn("LoginService: Username / Email from client payload is null. Throwing InvalidUserDetailsException.");
            throw new InvalidUserDetailsException("Username/Email identifier cannot be null!");
        }
        // PASSWORD NULL
        if (loginRequestDTO.getPassword()==null){
            logger.warn("LoginService: Password from client payload is null. Throwing InvalidUserDetailsException");
            throw new InvalidUserDetailsException("Password cannot be null!");
        }
        // USERNAME/EMAIL EMPTY
        if (loginRequestDTO.getIdentifier().isEmpty()){
            logger.warn("LoginService: Username / email from client payload is empty. Throwing InvalidUserDetailsException");
            throw new InvalidUserDetailsException("Username/email identifier is empty");
        }
        // Password EMPTY
        if (loginRequestDTO.getPassword().isEmpty()){
            logger.warn("LoginService: Password from client payload is empty. Throwing InvalidUserDetailsException");
            throw new InvalidUserDetailsException("Password is empty");
        }
    }

}
