package com.example.product_store.security.controller;

import com.example.product_store.security.dto.RolesDTO;
import com.example.product_store.security.jwt.JwtUtil;
import com.example.product_store.security.model.Account;
import com.example.product_store.security.model.LoginRequest;
import com.example.product_store.security.model.LoginResponse;
import com.example.product_store.security.model.MyUserDetails;
import com.example.product_store.security.service.CreateAccountService;
import com.example.product_store.security.service.GetRolesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final GetRolesService getRolesService;
    private final AuthenticationManager manager;
    private final CreateAccountService createNewAccountService;

    public AuthController(GetRolesService getRolesService, AuthenticationManager manager, CreateAccountService createNewAccountService) {
        this.getRolesService = getRolesService;
        this.manager = manager;
        this.createNewAccountService = createNewAccountService;
    }

    // GET ALL ROLES, DISPLAY ON FRONTEND REGISTRATION AS SELECTION
    @GetMapping("/getRoles")
    public ResponseEntity<List<RolesDTO>> getRoles(){
        List<RolesDTO> rolesDTOS = getRolesService.execute(null);
        return ResponseEntity.status(HttpStatus.OK).body(rolesDTOS);
    }

    // CREATE ACCOUNT
    @PostMapping("/createAccount")
    public ResponseEntity<String> createNewUser(@RequestBody Account account) {
        String response = createNewAccountService.execute(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(), loginRequest.getPassword());
        // this will fault if credentials not valid
        Authentication authentication = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = JwtUtil.generateToken((MyUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }




}
