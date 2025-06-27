package com.example.product_store.kafka.controller;

import com.example.product_store.kafka.service.OrderService;
import com.example.product_store.order.OrderCreationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderDemoController {

    private final OrderService orderService;

    public OrderDemoController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public String createOrder(@RequestBody List<OrderCreationRequest> request) {
        orderService.execute(request);
        return "Order request received!";
    }
}