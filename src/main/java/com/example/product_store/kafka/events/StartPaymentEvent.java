package com.example.product_store.kafka.events;

import com.example.product_store.kafka.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartPaymentEvent {

    private String orderId;
    private String customerId;
    private BigDecimal totalPrice;
    private LocalDateTime processedAt;
    private PaymentStatus paymentStatus;

    public StartPaymentEvent(OrderCreatedEvent event){
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.totalPrice = event.getTotalPrice();
        this.processedAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PROCESSING;
    }


}
