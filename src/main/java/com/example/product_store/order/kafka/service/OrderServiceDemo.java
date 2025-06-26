package com.example.product_store.order.kafka.service;


import com.example.product_store.order.kafka.dto.OrderRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceDemo {

    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;


    public OrderServiceDemo(KafkaTemplate<String, OrderRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void placeOrder(OrderRequest orderRequest) throws JsonProcessingException {
        kafkaTemplate.send("order-events",orderRequest);
    }
}

