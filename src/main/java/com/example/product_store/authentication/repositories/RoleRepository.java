package com.example.product_store.authentication.repositories;

import com.example.product_store.authentication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findById(String id); // likely already exist
}
