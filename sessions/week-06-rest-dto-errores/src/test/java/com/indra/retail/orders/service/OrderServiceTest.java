package com.indra.retail.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.model.OrderItem;
import com.indra.retail.orders.model.OrderStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    private final OrderService orderService = new OrderService();

    @Test
    @DisplayName("Debe crear un pedido con ID, estado y total calculado")
    void create_validRequest_returnsCreatedOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "customer-1",
                List.of(new OrderItem("SKU-1", 2, 15.50)),
                "123 Main Street");

        OrderResponse result = orderService.create(request);

        assertFalse(result.orderId().isBlank());
        assertEquals(OrderStatus.CREATED, result.status());
        assertEquals(31.00, result.totalAmount(), 0.001);
        assertEquals(LocalDate.now().plusDays(5), result.estimatedDelivery());
    }

    @Test
    @DisplayName("Debe devolver el pedido cuando existe el ID")
    void findById_existingOrder_returnsOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "customer-1",
                List.of(new OrderItem("SKU-1", 1, 20.00)),
                "123 Main Street");
        OrderResponse createdOrder = orderService.create(request);

        OrderResponse result = orderService.findById(createdOrder.orderId());

        assertEquals(createdOrder, result);
    }

    @Test
    @DisplayName("Debe lanzar OrderNotFoundException cuando el pedido no existe")
    void findById_missingOrder_throwsOrderNotFoundException() {
        String missingOrderId = "missing-order";

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.findById(missingOrderId));

        assertEquals("Pedido no encontrado: missing-order", exception.getMessage());
    }
}