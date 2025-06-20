package com.example.product_store.order.service;

import com.example.product_store.order.dto.KafkaOrderGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaAdminProducerService {

    private final KafkaTemplate<String, KafkaOrderGroup> kafkaTemplate;

    @Value("${kafka.topic.admin-orders}")
    private String adminTopic;

    public KafkaAdminProducerService(KafkaTemplate<String, KafkaOrderGroup> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendAdminOrderGroup(KafkaOrderGroup group) {
        kafkaTemplate.send(adminTopic, group.getAdminId(), group);
    }
}