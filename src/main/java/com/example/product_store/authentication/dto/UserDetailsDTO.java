package com.example.product_store.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO implements Serializable {
    private String id;
    private String username;
    private String hashedPassword;
    private Set<String> roles;

}

