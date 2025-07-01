package com.example.product_store.order.controller;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.service.GetOrderService;
import com.example.product_store.order.service.CreateOrderService;
import com.example.product_store.order.dto.OrderCreationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/orders")
public class OrderDemoController {

    private final CreateOrderService createOrderService;
    private final GetOrderService getOrderService;

    public OrderDemoController(CreateOrderService createOrderService, GetOrderService getOrderService) {
        this.createOrderService = createOrderService;
        this.getOrderService = getOrderService;
    }


    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderCreationRequest> request) {
        OrderDTO orderDTO =  createOrderService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable(name = "id") String id){
        OrderDTO orderDTO = getOrderService.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }
}