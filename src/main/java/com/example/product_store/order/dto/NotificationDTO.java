package com.example.product_store.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String adminId;
    private String batchOrderId;
    private String clientId;

    private BigDecimal totalPaid;
    private LocalDateTime createdAt;
    private String orderStatus;
}
