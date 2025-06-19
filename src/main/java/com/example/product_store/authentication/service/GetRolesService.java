package com.example.product_store.authentication.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.authentication.repositories.RoleRepository;
import com.example.product_store.authentication.dto.RolesDTO;
import com.example.product_store.authentication.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRolesService implements QueryBinder<Void,List<RolesDTO>> {

    private final RoleRepository roleRepository;

    public GetRolesService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    // GET THE ROLES
    // FOR CLIENT SIDE TO PUT ON SELECT BAR
    @Override
    public List<RolesDTO> execute(Void input){
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream().map(RolesDTO::new).toList();
    }

}
