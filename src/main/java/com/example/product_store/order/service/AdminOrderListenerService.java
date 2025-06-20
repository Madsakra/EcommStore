package com.example.product_store.order.service;

import com.example.product_store.order.dto.KafkaOrderGroup;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminOrderListenerService {
    private final Map<String, List<KafkaOrderGroup>> adminOrderStore = new ConcurrentHashMap<>();


    @KafkaListener(
            topics = "admin-notifications",
            groupId = "admin-group",
            containerFactory = "kafkaOrderGroupListenerFactory" // 👈 this is required
    )
    public void listen(KafkaOrderGroup group) {
        adminOrderStore.compute(group.getAdminId(), (adminId, existingGroups) -> {
            if (existingGroups == null) {
                existingGroups = new ArrayList<>();
            }
            existingGroups.add(group);
            return existingGroups;
        });
    }

    public List<KafkaOrderGroup> getOrdersByAdminId(String adminId) {
        return adminOrderStore.getOrDefault(adminId, Collections.emptyList());
    }
}
