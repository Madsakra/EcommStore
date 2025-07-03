package com.example.product_store.order.controller;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.service.GetOrderService;
import com.example.product_store.order.service.CreateOrderService;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name="Orders Management",
        description = "APIs for managing orders within the system. For User account only."
)
@RestController
@RequestMapping("/user/orders")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final GetOrderService getOrderService;

    public OrderController(CreateOrderService createOrderService, GetOrderService getOrderService) {
        this.createOrderService = createOrderService;
        this.getOrderService = getOrderService;
    }

    // ORDER CREATION -> BY USERS
    @Operation(
            summary = "Create an order",
            description = "Create an order out of the cart for users. Only usable by user accounts.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order Successfully created",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class)))
            })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderCreationRequest> request) {
        OrderDTO orderDTO =  createOrderService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    // GET ORDERS -> BY USERS
    @Operation(
            summary = "Get order information",
            description = "Get order information and check status of an order. Only usable by user accounts.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order Successfully retrieved",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable(name = "id") String id){
        OrderDTO orderDTO = getOrderService.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }
}