package com.indra.retail.orders.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.indra.retail.orders.dto.CreateOrderRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class CreateOrderRequestValidationTest {

    @Test
    void validatesRequiredFieldsAndAddressLength() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            var missingValues = validator.validate(new CreateOrderRequest(null, null, null));
            assertEquals(Set.of("customerId", "items", "deliveryAddress"),
                    missingValues.stream()
                            .map(violation -> violation.getPropertyPath().toString())
                            .collect(Collectors.toSet()));

            var emptyValues = validator.validate(new CreateOrderRequest("", List.of(), "123 Main"));
            assertEquals(Set.of("customerId", "items", "deliveryAddress"),
                    emptyValues.stream()
                            .map(violation -> violation.getPropertyPath().toString())
                            .collect(Collectors.toSet()));

            var validRequest = new CreateOrderRequest("customer-1", List.of(new OrderItem()), "123 Main Street");
            assertTrue(validator.validate(validRequest).isEmpty());
        }
    }
}