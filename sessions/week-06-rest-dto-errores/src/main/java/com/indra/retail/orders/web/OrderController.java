package com.indra.retail.orders.web;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable @NotBlank String orderId) {
        OrderResponse response = orderService.findById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
