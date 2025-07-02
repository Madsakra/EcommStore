package com.example.product_store.authentication.dto;

import com.example.product_store.authentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RolesDTO implements Serializable {

    private String roleId;
    private String roleName;

    public RolesDTO(Role role){
        this.roleId = role.getRoleId();
        this.roleName = role.getRoleName();
    }

}
