package com.indra.retail.orders.service;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.model.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public OrderResponse create(CreateOrderRequest order) {
        Order newOrder = new Order(order.customerId(), order.items(), order.deliveryAddress());
        orders.put(newOrder.getId(), newOrder);
        return OrderResponse.from(newOrder);
    }



    public OrderResponse findById(String orderId)  {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }
        return OrderResponse.from(order);
    }


}
