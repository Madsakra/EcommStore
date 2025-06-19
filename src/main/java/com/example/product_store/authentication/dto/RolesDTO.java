package com.example.product_store.authentication.dto;

import com.example.product_store.authentication.model.Role;
import lombok.Data;

import java.io.Serializable;

@Data
public class RolesDTO implements Serializable {

    private String roleId;
    private String roleName;

    public RolesDTO(Role role){
        this.roleId = role.getRoleId();
        this.roleName = role.getRoleName();
    }

}
