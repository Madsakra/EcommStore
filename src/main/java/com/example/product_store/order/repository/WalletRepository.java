package com.example.product_store.order.repository;

import com.example.product_store.order.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,String> {
    Optional<Wallet> findByClientId(String clientId);
}
