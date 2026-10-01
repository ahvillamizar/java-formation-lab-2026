package com.indra.retail.orders.web;

import com.indra.retail.orders.service.OrderNotFoundException;
import com.indra.retail.orders.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionHandlerControllerTest {

    private MockMvc mockMvc;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(new ExceptionHandlerController())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("Debe convertir pedido inexistente en 404")
    void handleOrderNotFound() {
        var exception = new OrderNotFoundException("order-1");

        var response = new ExceptionHandlerController().handleOrderNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Pedido no encontrado: order-1", response.getBody().errors().getFirst());
    }

    @Test
    @DisplayName("Debe responder 400 con los campos inválidos del body")
        void handleMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3));
    }



    @Test
    @DisplayName("Debe responder 500 sin exponer detalles internos")
    void handleGenericException() {
        var response = new ExceptionHandlerController().handleGenericException(
                new IllegalStateException("detalle interno"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().status());
        assertEquals("Error interno del servidor", response.getBody().errors().getFirst());
    }
}