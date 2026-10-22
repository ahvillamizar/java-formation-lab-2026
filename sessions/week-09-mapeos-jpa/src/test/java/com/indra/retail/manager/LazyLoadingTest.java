package com.indra.retail.manager;

import com.indra.retail.entities.Customer;
import com.indra.retail.entities.Order;
import com.indra.retail.entities.OrderItem;
import com.indra.retail.entities.Product;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LazyLoadingTest {


    private final EntityManagerRecordManager manager;
    private final TransactionTemplate transactionTemplate;

    private Long orderId;

    @Autowired
    public LazyLoadingTest(EntityManagerRecordManager manager, TransactionTemplate transactionTemplate) {
        this.manager = manager;
        this.transactionTemplate = transactionTemplate;
    }

    @BeforeEach
    void createOrderWithItems() {
        orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Carla", UUID.randomUUID() + "@indra.com"));
            Product product = manager.create(new Product("Laptop", new BigDecimal("1500.00"), 4));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });
    }

    @Test
    @DisplayName("Acceder a items fuera de la transacción lanza LazyInitializationException")
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        Order order = manager.findById(Order.class, orderId).orElseThrow();
        assertThrows(LazyInitializationException.class, () -> order.getItems().size());
    }

    @Test
    @DisplayName("Acceder a items dentro de una transacción activa funciona")
    void shouldLoadItemsInsideTransaction() {
        int itemCount = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getItems().size();
        });

        assertEquals(1, itemCount);
    }



    @Test
    @DisplayName("Customer de un Order no se carga hasta que se usa (LAZY)")
    void shouldLoadCustomerLazily() {
        Order detachedOrder = manager.findById(Order.class, orderId).orElseThrow();

        assertThrows(LazyInitializationException.class,
                () -> detachedOrder.getCustomer().getName());

        String customerName = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getCustomer().getName();
        });

        assertEquals("Carla", customerName);
    }

    @Test
    @DisplayName("Quitar un ítem de la lista elimina la fila (orphanRemoval)")
    void shouldRemoveOrphanItem() {
        Long itemId = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem item = order.getItems().get(0);
            Long removedItemId = item.getId();
            order.getItems().remove(item);
            return removedItemId;
        });

        assertFalse(manager.findById(OrderItem.class, itemId).isPresent());
    }

    @Test
    @DisplayName("No se puede guardar un OrderItem sin Order")
    void shouldRejectOrphanOrderItem() {
        assertThrows(DataIntegrityViolationException.class, () -> transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem existingItem = order.getItems().get(0);
            manager.create(new OrderItem(
                    existingItem.getProduct(),
                    existingItem.getQuantity(),
                    existingItem.getUnitPrice()));
            return null;
        }));
    }

    @Test
    @DisplayName("Borrar un OrderItem no borra el Product")
    void shouldNotDeleteProductWhenDeletingItem() {
        Long[] ids = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem item = order.getItems().get(0);
            return new Long[] {item.getId(), item.getProduct().getId()};
        });

        assertTrue(manager.delete(OrderItem.class, ids[0]));
        assertTrue(manager.findById(Product.class, ids[1]).isPresent());
    }
}
