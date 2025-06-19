package com.example.product_store.order;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.service.CreateOrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class OrderController {
  private final CreateOrderService createOrderService;

  public OrderController(CreateOrderService createOrderService) {
    this.createOrderService = createOrderService;
  }

  @PostMapping("/order")
  public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderCreationRequest> request) {
    OrderDTO orderDTO = createOrderService.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
  }
}
