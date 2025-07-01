package com.example.product_store.notification.dto;

import com.example.product_store.notification.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private String notificationId;
    private String batchOrderId;
    private String clientId;
    private BigDecimal totalPaid;
    private LocalDateTime createdAt;
    private String orderStatus;

    public NotificationDTO(Notification notification){
        this.notificationId = notification.getNotificationId();
        this.batchOrderId = notification.getBatchOrderId();
        this.clientId = notification.getClientId();
        this.totalPaid = notification.getTotalPaid();
        this.createdAt = notification.getCreatedAt();
        this.orderStatus = notification.getOrderStatus();
    }

}
