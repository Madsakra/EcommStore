package com.example.product_store.authentication;

import com.example.product_store.authentication.dto.AccountDTO;
import com.example.product_store.authentication.dto.LoginResponseDTO;
import com.example.product_store.authentication.dto.RolesDTO;
import com.example.product_store.authentication.jwt.JwtUtil;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.dto.LoginRequestDTO;
import com.example.product_store.authentication.jwt.MyUserDetails;
import com.example.product_store.authentication.service.CreateAccountService;
import com.example.product_store.authentication.service.GetRolesService;
import com.example.product_store.authentication.service.LoginService;
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
    private final LoginService loginService;

    public AuthController(GetRolesService getRolesService,
                          AuthenticationManager manager,
                          CreateAccountService createNewAccountService,
                          LoginService loginService) {
        this.getRolesService = getRolesService;
        this.manager = manager;
        this.createNewAccountService = createNewAccountService;
        this.loginService = loginService;
    }

    // GET ALL ROLES, DISPLAY ON FRONTEND REGISTRATION AS SELECTION
    @GetMapping("/getRoles")
    public ResponseEntity<List<RolesDTO>> getRoles(){
        List<RolesDTO> rolesDTOS = getRolesService.execute(null);
        return ResponseEntity.status(HttpStatus.OK).body(rolesDTOS);
    }

    // CREATE ACCOUNT
    @PostMapping("/createAccount")
    public ResponseEntity<AccountDTO> createNewUser(@RequestBody Account account) {
        AccountDTO response = createNewAccountService.execute(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String jwtToken = loginService.execute(loginRequestDTO);
        return ResponseEntity.ok(new LoginResponseDTO(jwtToken));
    }




}
