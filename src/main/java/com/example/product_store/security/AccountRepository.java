package com.example.product_store.security;

import com.example.product_store.security.model.Account;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
  Optional<Account> findByUserName(String userName);

  // CUSTOM JPA QUERY
  // TO FOLLOW ENTITY CLASS NAMING CONVENTION
  @EntityGraph(attributePaths = "roles")
  @Query("from Account where email = :i or userName = :i")
  Optional<Account> findUserByEmailOrUserName(@Param("i") String i);
}
