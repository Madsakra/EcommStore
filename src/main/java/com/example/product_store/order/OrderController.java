package com.example.product_store.order;


import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.service.OrderProcessingService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
  private final OrderProcessingService orderProcessingService;

  public OrderController(OrderProcessingService orderProcessingService) {
    this.orderProcessingService = orderProcessingService;
  }

  @PostMapping("/user/order")
  public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderCreationRequest> request) {
    OrderDTO orderDTO = orderProcessingService.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
  }

  // FOR ADMINS TO GET ORDER
  @GetMapping("/{adminId}")
  public Void getOrders(@PathVariable String adminId) {
    return null;
  }


}
