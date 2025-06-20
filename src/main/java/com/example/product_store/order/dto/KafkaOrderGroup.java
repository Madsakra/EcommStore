package com.example.product_store.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaOrderGroup {
    private String adminId;
    private String batchOrderId;
    private List<KafkaOrderItem> kafkaOrderItems;
    private LocalDateTime createdAt;
}
