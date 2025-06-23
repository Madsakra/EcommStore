package com.example.product_store.authentication.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.dto.LoginRequestDTO;
import com.example.product_store.authentication.errors.InvalidUserDetailsException;
import com.example.product_store.authentication.jwt.JwtUtil;
import com.example.product_store.authentication.jwt.MyUserDetails;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements Command<LoginRequestDTO,String> {

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

        // this will fault if credentials not valid
        Authentication authentication = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = JwtUtil.generateToken((MyUserDetails) authentication.getPrincipal());
        return jwtToken;
    }

    // CHECK IF PAYLOAD CONSISTS OF NULL / EMPTY
    private void checkRequestDTO(LoginRequestDTO loginRequestDTO) {
        // USERNAME NULL
        if (loginRequestDTO.getIdentifier()==null){
            throw new InvalidUserDetailsException("Username/Email identifier cannot be null!");
        }
        // PASSWORD NULL
        if (loginRequestDTO.getPassword()==null){
            throw new InvalidUserDetailsException("Password cannot be null!");
        }
        // USERNAME/EMAIL EMPTY
        if (loginRequestDTO.getIdentifier().isEmpty()){
            throw new InvalidUserDetailsException("Username/email identifier is empty");
        }
        // Password EMPTY
        if (loginRequestDTO.getPassword().isEmpty()){
            throw new InvalidUserDetailsException("Password is empty");
        }
    }

}
