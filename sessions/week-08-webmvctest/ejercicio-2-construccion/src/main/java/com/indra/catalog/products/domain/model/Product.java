package com.indra.catalog.products.domain.model;

import java.math.BigDecimal;

public record Product(String id, String name, BigDecimal price, int stock) {

    public Product {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        if (name == null || name.isBlank() || name.length() > 120) {
            throw new IllegalArgumentException("El nombre debe tener entre 1 y 120 caracteres");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}