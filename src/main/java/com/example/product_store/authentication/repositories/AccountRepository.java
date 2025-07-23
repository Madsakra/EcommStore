package com.example.product_store.authentication.repositories;

import com.example.product_store.authentication.model.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  // FOR ACCOUNT CREATION
  boolean existsByUserNameOrEmail(String userName, String email);

  // USED FOR LOGGING IN
  // CHECK WHETHER USER EXISTS WITH USERNAME / EMAIL
  // DUE TO CONNECTION BETWEEN ROLES AND ACCOUNT
  // HENCE -> EntityGraph (attributePaths = "roles")
  @EntityGraph(attributePaths = "roles")
  @Query("from Account where email = :u or userName = :u")
  Optional<Account> findUserByEmailOrUserName(@Param("u") String u);
}
