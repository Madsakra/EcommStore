package com.example.product_store.security.dto;

import com.example.product_store.security.model.Role;
import lombok.Data;

@Data
public class RolesDTO {

    private String roleId;
    private String roleName;

    public RolesDTO(Role role){
        this.roleId = role.getRoleId();
        this.roleName = role.getRoleName();
    }

}
