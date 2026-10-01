package com.indra.retail.orders.dto;

import com.indra.retail.orders.model.Order;
import com.indra.retail.orders.model.OrderStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderResponseTest {

    @Test
    @DisplayName("Debe copiar los datos del pedido al DTO de respuesta")
    void from_copiesOrderFields() {
        var order = new Order();
        order.setId("order-1");
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(21.0);
        order.setEstimatedDelivery(LocalDate.of(2026, 10, 4));

        OrderResponse response = OrderResponse.from(order);

        assertEquals("order-1", response.orderId());
        assertEquals(OrderStatus.CREATED, response.status());
        assertEquals(21.0, response.totalAmount());
        assertEquals(LocalDate.of(2026, 10, 4), response.estimatedDelivery());
    }
}