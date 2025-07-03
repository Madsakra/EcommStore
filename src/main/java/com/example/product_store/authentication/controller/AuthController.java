package com.example.product_store.authentication.controller;

import com.example.product_store.authentication.dto.*;
import com.example.product_store.authentication.service.CreateAccountService;
import com.example.product_store.authentication.service.GetRolesService;
import com.example.product_store.authentication.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
 name="Authentication Management",
        description = "APIs for managing authentication and security"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final GetRolesService getRolesService;
  private final CreateAccountService createNewAccountService;
  private final LoginService loginService;

  public AuthController(
      GetRolesService getRolesService,
      CreateAccountService createNewAccountService,
      LoginService loginService) {
    this.getRolesService = getRolesService;
    this.createNewAccountService = createNewAccountService;
    this.loginService = loginService;
  }

  @Operation(
      summary = "Get all Roles",
      description = "Returns all roles for account creation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved",
            content = @Content(schema = @Schema(implementation = RolesDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "No Roles found",
            content = @Content(schema = @Schema()))
      })
  // GET ALL ROLES, DISPLAY ON FRONTEND REGISTRATION AS SELECTION
  @GetMapping("/getRoles")
  public ResponseEntity<List<RolesDTO>> getRoles() {
    List<RolesDTO> rolesDTOS = getRolesService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(rolesDTOS);
  }

  // CREATE ACCOUNT
  @Operation(
      summary = "Create an account",
      description = "Create an account for accessing server resources")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Account already exist when trying to create.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Role id don't exist.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict: Account already exists or role ID/name mismatch",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Failed to create Account due to invalid parameters.",
            content = @Content(schema = @Schema())),
      })
  @PostMapping("/createAccount")
  public ResponseEntity<AccountDTO> createNewUser(
      @RequestBody AccountRequestDTO request) {
    System.out.println(request);
    AccountDTO response = createNewAccountService.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // LOGIN
  @Operation(
      summary = "Login",
      description = "Login with your username / email and password")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created",
            content =
                @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Account don't exist or Invalid login credentials",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "401",
            description = "Failed to login due to wrong credentials.",
            content = @Content(schema = @Schema())),
      })
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(
      @RequestBody LoginRequestDTO loginRequestDTO) {
    String jwtToken = loginService.execute(loginRequestDTO);
    return ResponseEntity.ok(new LoginResponseDTO(jwtToken));
  }
}
