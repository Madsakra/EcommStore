package com.example.product_store.order.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.exceptions.OrderNotFoundException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetOrderService implements QueryBinder<String, OrderDTO> {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDTO execute(String id){
       Optional<Order> orderOptional = orderRepository.findById(id);
       if (orderOptional.isPresent()){
           return new OrderDTO(orderOptional.get());
       }

       throw new OrderNotFoundException("Current order with id not found");

    }

}

