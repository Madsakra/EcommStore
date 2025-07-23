package com.example.product_store.authentication;

import com.example.product_store.authentication.dto.RolesDTO;
import com.example.product_store.authentication.errors.InvalidRoleIdException;
import com.example.product_store.authentication.errors.RoleMismatchException;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.RoleRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidatorUtil {

  // USED SOLELY FOR ACCOUNT CREATION

  private final RoleRepository roleRepository;
  private final Logger logger = LoggerFactory.getLogger(RoleValidatorUtil.class);

  // check if the roles from payload is valid
  // if valid will return a set of roles in this format:
  // Role{id=1, name='ROLE_USER'}
  public Set<Role> validateRoles(Set<RolesDTO> inputRoles) {
    Set<Role> validatedRoles = new HashSet<>();
    for (RolesDTO role : inputRoles) {
      logger.info("Validating role: ID={}, Name={}", role.getRoleId(), role.getRoleName());

      Role dbRole =
          roleRepository
              .findById(role.getRoleId())
              .orElseThrow(
                  () -> {
                    logger.warn("Role ID not found: {}", role.getRoleId());
                    return new InvalidRoleIdException("Invalid role ID: " + role.getRoleId());
                  });

      // if role name in payload does not match the id
      if (!dbRole.getRoleName().equals(role.getRoleName())) {
        logger.warn(
            "Role name mismatch. Input: '{}', Expected: '{}' for ID: {}",
            role.getRoleName(),
            dbRole.getRoleName(),
            role.getRoleId());
        throw new RoleMismatchException("Role name mismatch for ID: " + role.getRoleId());
      }

      logger.info("Role validated successfully: {}", dbRole);
      validatedRoles.add(dbRole);
    }

    logger.info("All roles validated successfully");
    return validatedRoles;
  }
}
