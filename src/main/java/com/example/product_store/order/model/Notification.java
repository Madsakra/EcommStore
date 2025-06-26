package com.example.product_store.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="notification_id")
    private String notificationId;


    private String adminId;
    private String batchOrderId;
    private String clientId;

    private BigDecimal totalPaid;
    private LocalDateTime createdAt;
    private String orderStatus;
}
