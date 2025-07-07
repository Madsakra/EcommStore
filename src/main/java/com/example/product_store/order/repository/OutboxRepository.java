package com.example.product_store.order.repository;

import com.example.product_store.order.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent,String> {

}
