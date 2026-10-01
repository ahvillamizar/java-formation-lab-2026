package com.indra.retail.orders.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    @DisplayName("Debe crear un ErrorResponse con timestamp y lista de errores")
    void shouldCreateErrorResponseWithTimestampAndErrors() {
        Instant before = Instant.now();
        ErrorResponse response = new ErrorResponse(Instant.now(), 400, List.of("customerId: no puede estar vacío"));
        Instant after = Instant.now();

        assertEquals(400, response.status());
        assertEquals(List.of("customerId: no puede estar vacío"), response.errors());
        assertNotNull(response.timestamp());
        assertFalse(response.timestamp().isBefore(before));
        assertFalse(response.timestamp().isAfter(after));
    }

    @Test
    @DisplayName("Debe inicializar el timestamp automáticamente con el constructor de lista")
    void shouldInitializeTimestampWhenUsingListConstructor() {
        ErrorResponse response = new ErrorResponse(404, List.of("Pedido no encontrado"));

        assertEquals(404, response.status());
        assertEquals(List.of("Pedido no encontrado"), response.errors());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe inicializar el timestamp automáticamente con el constructor de string")
    void shouldInitializeTimestampWhenUsingStringConstructor() {
        ErrorResponse response = new ErrorResponse(500, "Error interno del servidor");

        assertEquals(500, response.status());
        assertEquals(List.of("Error interno del servidor"), response.errors());
        assertNotNull(response.timestamp());
    }
}