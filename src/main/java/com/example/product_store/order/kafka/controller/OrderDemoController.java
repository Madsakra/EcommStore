package com.example.product_store.order.kafka.controller;

import com.example.product_store.order.kafka.dto.OrderRequest;
import com.example.product_store.order.kafka.service.OrderServiceDemo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderDemoController {

    private final OrderServiceDemo orderServiceDemo;

    public OrderDemoController(OrderServiceDemo orderServiceDemo) {
        this.orderServiceDemo = orderServiceDemo;
    }


    @PostMapping
    public String createOrder(@RequestBody OrderRequest request) throws JsonProcessingException {
        orderServiceDemo.placeOrder(request);
        return "Order request received!";
    }
}