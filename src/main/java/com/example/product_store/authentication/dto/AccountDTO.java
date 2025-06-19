package com.example.product_store.authentication.dto;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class AccountDTO {

    private String id;
    private String username;
    private String email;
    private Set<Role> roles;

    public AccountDTO(Account account){
        this.id = account.getId();
        this.username = account.getUserName();
        this.email = account.getEmail();
        this.roles = account.getRoles();
    }

}
