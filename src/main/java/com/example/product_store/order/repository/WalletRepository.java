package com.example.product_store.order.repository;

import com.example.product_store.order.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,String> {
    Optional<Wallet> findByClientId(String clientId);
}
