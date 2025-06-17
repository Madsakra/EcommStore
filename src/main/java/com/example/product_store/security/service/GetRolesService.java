package com.example.product_store.security.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.security.RoleRepository;
import com.example.product_store.security.dto.RolesDTO;
import com.example.product_store.security.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRolesService implements QueryBinder<Void,List<RolesDTO>> {

    private final RoleRepository roleRepository;

    public GetRolesService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RolesDTO> execute(Void input){
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream().map(RolesDTO::new).toList();
    }

}
