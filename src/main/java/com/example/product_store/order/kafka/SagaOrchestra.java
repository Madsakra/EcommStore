package com.example.product_store.order.kafka;

import com.example.product_store.order.kafka.dto.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SagaOrchestra {

    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;
    public static final Logger logger = LoggerFactory.getLogger(SagaOrchestra.class);

    public SagaOrchestra(KafkaTemplate<String, OrderRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    // CONSUME ORDER EVENT FIRST
    @KafkaListener(topics = "order-events",groupId = "saga-group")
    public void handlerOrderEvent(OrderRequest event) {
        logger.info(
                "Received Order at Saga Orchestra:{}. Time of receipt:{}",
                event.getOrderId(),
                LocalDateTime.now());
            // START PAYMENT EVENT
            // SENDS PAYMENT EVENT -> PAYMENT SERVICE
            kafkaTemplate.send("payment-commands",event);

            // AT THE SAME TIME -> START INVENTORY SERVICE
            kafkaTemplate.send("inventory-commands",event);
    }

    // CONSUME PAYMENT RESPONSE
    @KafkaListener(topics = "payment-events",groupId = "saga-group")
    public void handlePaymentEvent(OrderRequest event){
        logger.info("Received payment order at Saga Orchestra for handlePaymentEvent. " +
                "Time of receipt:{}", LocalDateTime.now());


    }

    // CONSUME INVENTORY EVENTS FIRST
    @KafkaListener(topics="inventory-events",groupId = "saga-group")
    public void handleInventoryEvent(OrderRequest event){
        logger.info("Received inventory order at Saga Orchestra for handleInventoryEvent " +
                "Time of receipt:{}", LocalDateTime.now());
    }




}
