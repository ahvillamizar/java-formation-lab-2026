package com.indra.catalog.products.web;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.infrastructure.security.SecurityConfig;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getExistingProductReturnsProductJson() throws Exception {
        when(productService.getById("PRD-0001"))
                .thenReturn(new Product("PRD-0001", "Laptop", new BigDecimal("3500000.00"), 10));

        mockMvc.perform(get("/api/products/PRD-0001"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PRD-0001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(3500000.00))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void getMissingProductReturns404() throws Exception {
        when(productService.getById("PRD-9999")).thenThrow(new ProductNotFoundException("PRD-9999"));

        mockMvc.perform(get("/api/products/PRD-9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0]").value("Producto no encontrado: PRD-9999"));
    }

    @Test
    void createValidProductReturns201AndLocation() throws Exception {
        when(productService.create("Laptop", new BigDecimal("3500000.00"), 10))
                .thenReturn(new Product("PRD-0001", "Laptop", new BigDecimal("3500000.00"), 10));

        mockMvc.perform(post("/api/products")
                        .with(httpBasic("editor", "change-me-editor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"price\":3500000.00,\"stock\":10}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "http://localhost/api/products/PRD-0001"))
                .andExpect(jsonPath("$.id").value("PRD-0001"));
    }

    @Test
    void createInvalidProductReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(httpBasic("editor", "change-me-editor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":12.50,\"stock\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("name")));
    }

    @Test
    void deleteProductReturns204() throws Exception {
        doNothing().when(productService).delete("PRD-0001");

        mockMvc.perform(delete("/api/products/PRD-0001")
                        .with(httpBasic("admin", "change-me-admin")))
                .andExpect(status().isNoContent())
                .andExpect(header().doesNotExist("Content-Type"))
                .andExpect(content().string(""));
    }

    @Test
    void anonymousPostReturns401() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"price\":12.50,\"stock\":1}"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Content-Type"));
    }

    @Test
    void editorCannotDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/PRD-0001")
                        .with(httpBasic("editor", "change-me-editor")))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Content-Type"));
    }
}