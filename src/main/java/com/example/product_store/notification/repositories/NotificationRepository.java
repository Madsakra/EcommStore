package com.example.product_store.notification.repositories;

import com.example.product_store.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,String> {
    Optional<Notification> findFirstByAdminIdOrderByCreatedAtDesc(String adminId);

}
