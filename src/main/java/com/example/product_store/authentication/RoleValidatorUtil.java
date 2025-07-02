package com.example.product_store.authentication;

import com.example.product_store.authentication.dto.RolesDTO;
import com.example.product_store.authentication.errors.InvalidRoleIdException;
import com.example.product_store.authentication.errors.RoleMismatchException;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleValidatorUtil {

    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(RoleValidatorUtil.class);

    public Set<Role> validateRoles(Set<RolesDTO> inputRoles) {
        Set<Role> validatedRoles = new HashSet<>();

        for (RolesDTO role : inputRoles) {
            logger.info("Validating role: ID={}, Name={}", role.getRoleId(), role.getRoleName());

            Role dbRole = roleRepository.findById(role.getRoleId())
                    .orElseThrow(() -> {
                        logger.warn("Role ID not found: {}", role.getRoleId());
                        return new InvalidRoleIdException("Invalid role ID: " + role.getRoleId());
                    });

            if (!dbRole.getRoleName().equals(role.getRoleName())) {
                logger.warn("Role name mismatch. Input: '{}', Expected: '{}' for ID: {}",
                        role.getRoleName(), dbRole.getRoleName(), role.getRoleId());
                throw new RoleMismatchException("Role name mismatch for ID: " + role.getRoleId());
            }

            logger.info("Role validated successfully: {}", dbRole);
            validatedRoles.add(dbRole);
        }

        logger.info("All roles validated successfully");
        return validatedRoles;
    }
}
