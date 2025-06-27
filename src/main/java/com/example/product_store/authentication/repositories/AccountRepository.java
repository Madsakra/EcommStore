package com.example.product_store.authentication.repositories;

import com.example.product_store.authentication.model.Account;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
  Optional<Account> findByUserName(String userName);

  // FOR ACCOUNT CREATION
  boolean existsByUserNameOrEmail(String userName, String email);

  // CUSTOM JPA QUERY
  // USED FOR LOGGING IN
  // CHECK WHETHER USER EXISTS WITH USERNAME / EMAIL
  @EntityGraph(attributePaths = "roles")
  @Query("from Account where email = :u or userName = :u")
  Optional<Account> findUserByEmailOrUserName(@Param("u") String u);


  // FOR UPDATING ACCOUNT BALANCE
  // PREVENT CONCURRENT UPDATE CRASH
  @Query("SELECT a FROM Account a WHERE a.id = :id")
  Optional<Account> findByIdForUpdate(@Param("id") String id);
}
