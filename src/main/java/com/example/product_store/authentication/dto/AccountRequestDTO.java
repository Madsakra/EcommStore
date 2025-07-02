package com.example.product_store.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {
    private String username;
    private String email;
    private String password;
    private Set<RolesDTO> roles;
}
