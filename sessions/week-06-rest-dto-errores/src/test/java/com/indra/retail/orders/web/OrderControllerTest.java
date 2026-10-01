package com.indra.retail.orders.web;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.model.OrderItem;
import com.indra.retail.orders.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

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
    @DisplayName("Debe crear un pedido válido y responder 201")
    void create_validRequest_returnsCreated() throws Exception {
    mockMvc.perform(post("/api/orders")
            .contentType("application/json")
            .content("""
                {
                  "customerId": "customer-1",
                  "items": [{"sku": "SKU-1", "quantity": 2, "unitPrice": 10.5}],
                  "deliveryAddress": "123 Main Street"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId").isNotEmpty())
        .andExpect(jsonPath("$.status").value("CREATED"))
        .andExpect(jsonPath("$.totalAmount").value(21.0));
    }

    @Test
    @DisplayName("Debe rechazar un pedido sin los campos obligatorios")
    void create_invalidRequest_returnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/orders")
            .contentType("application/json")
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors.length()").value(3));
    }

    @Test
    @DisplayName("Debe devolver un pedido existente y responder 200")
    void getById_existingOrder_returnsOk() throws Exception {
    var created = orderService.create(new CreateOrderRequest(
        "customer-1",
        List.of(new OrderItem("SKU-1", 2, 10.5)),
        "123 Main Street"));

    mockMvc.perform(get("/api/orders/{orderId}", created.orderId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(created.orderId()))
        .andExpect(jsonPath("$.status").value("CREATED"))
        .andExpect(jsonPath("$.totalAmount").value(21.0));
    }



    @Test
    @DisplayName("Debe responder 404 cuando el pedido no existe")
    void getById_unknownOrder_returnsNotFound() throws Exception {
    mockMvc.perform(get("/api/orders/{orderId}", "unknown-order"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.errors[0]")
            .value("Pedido no encontrado: unknown-order"));
    }
}