package com.indra.catalog.products.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.domain.port.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductServiceImplTest {

    private ProductRepository repository;
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductServiceImpl(repository);
    }

    @Test
    void createsProductWithGeneratedIdAndExactPrice() {
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product created = service.create("Laptop", new BigDecimal("3500000.00"), 10);

        assertEquals("PRD-0001", created.id());
        assertEquals(new BigDecimal("3500000.00"), created.price());
        verify(repository).save(created);
    }

    @Test
    void findsExistingProduct() {
        Product product = new Product("PRD-0001", "Laptop", BigDecimal.TEN, 10);
        when(repository.findById(product.id())).thenReturn(Optional.of(product));

        assertSame(product, service.getById(product.id()));
    }

    @Test
    void throwsWhenProductDoesNotExist() {
        when(repository.findById("PRD-9999")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class, () -> service.getById("PRD-9999"));

        assertEquals("Producto no encontrado: PRD-9999", exception.getMessage());
    }

    @Test
    void deletesExistingProductAndRejectsMissingProduct() {
        when(repository.deleteById("PRD-0001")).thenReturn(true);
        service.delete("PRD-0001");
        verify(repository).deleteById("PRD-0001");

        when(repository.deleteById("PRD-9999")).thenReturn(false);
        assertThrows(ProductNotFoundException.class, () -> service.delete("PRD-9999"));
    }

    @Test
    void rejectsInvalidProductBeforePersistence() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("Laptop", BigDecimal.ZERO, 1));

        verify(repository, never()).save(any());
    }
}