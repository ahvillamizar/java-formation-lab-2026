package com.indra.catalog.products.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void preservesValidValuesAndMoneyPrecision() {
        BigDecimal price = new BigDecimal("3500000.00");

        Product product = new Product("PRD-0001", "Laptop", price, 10);

        assertEquals("PRD-0001", product.id());
        assertEquals("Laptop", product.name());
        assertEquals(price, product.price());
        assertEquals(10, product.stock());
    }

    @Test
    void rejectsMissingId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(" ", "Laptop", BigDecimal.ONE, 0));
    }

    @Test
    void rejectsBlankOrTooLongName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("PRD-0001", " ", BigDecimal.ONE, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Product("PRD-0001", "a".repeat(121), BigDecimal.ONE, 0));
    }

    @Test
    void rejectsNonPositivePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("PRD-0001", "Laptop", BigDecimal.ZERO, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Product("PRD-0001", "Laptop", new BigDecimal("-1"), 1));
    }

    @Test
    void rejectsNegativeStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("PRD-0001", "Laptop", BigDecimal.ONE, -1));
    }
}