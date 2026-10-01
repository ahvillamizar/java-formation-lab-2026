package com.indra.retail.orders.dto;

import com.indra.retail.orders.model.OrderItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateOrderRequestTest {

	@Test
	@DisplayName("No debe producir errores para una solicitud válida")
	void validRequest_hasNoConstraintViolations() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			var request = new CreateOrderRequest(
					"customer-1",
					List.of(new OrderItem("SKU-1", 2, 10.5)),
					"123 Main Street");

			assertTrue(validator.validate(request).isEmpty());
		}
	}

	@Test
	@DisplayName("Debe rechazar los campos obligatorios nulos")
	void nullRequiredFields_returnsViolationsForAllFields() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			var request = new CreateOrderRequest(null, null, null);

			var violations = validator.validate(request);
			Set<String> invalidFields = violations.stream()
					.map(violation -> violation.getPropertyPath().toString())
					.collect(Collectors.toSet());

			assertEquals(Set.of("customerId", "items", "deliveryAddress"), invalidFields);
		}
	}

	@Test
	@DisplayName("Debe validar template y mensaje para los campos obligatorios nulos")
	void nullRequiredFields_returnsExpectedTemplatesAndMessages() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			var request = new CreateOrderRequest(null, null, null);
			var violations = validator.validate(request);

            assertViolation(violations, "customerId", "{orders.customerId.notBlank}");
            assertViolation(violations, "items", "{orders.items.notEmpty}");
            assertViolation(violations, "deliveryAddress", "{orders.deliveryAddress.notBlank}");
		}
	}

	@Test
	@DisplayName("Debe rechazar una lista de items vacía")
	void emptyItems_returnsViolationForItems() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			var request = new CreateOrderRequest(
					"customer-1",
					List.of(),
					"123 Main Street");

			var violations = validator.validate(request);
			Set<String> invalidFields = violations.stream()
					.map(violation -> violation.getPropertyPath().toString())
					.collect(Collectors.toSet());

			assertEquals(Set.of("items"), invalidFields);
		}
	}

	@Test
	@DisplayName("Debe rechazar valores en blanco y una dirección corta")
	void blankValuesAndShortAddress_returnsViolations() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			var request = new CreateOrderRequest(
					"  ",
					List.of(new OrderItem("SKU-1", 1, 10.0)),
					"123 Main");

			var violations = validator.validate(request);
			Set<String> invalidFields = violations.stream()
					.map(violation -> violation.getPropertyPath().toString())
					.collect(Collectors.toSet());

			assertEquals(Set.of("customerId", "deliveryAddress"), invalidFields);

		}
	}

	private void assertViolation(
			Set<ConstraintViolation<CreateOrderRequest>> violations,
			String field,
			String expectedTemplate) {
		var violation = violations.stream()
				.filter(candidate -> candidate.getPropertyPath().toString().equals(field))
				.filter(candidate -> candidate.getMessageTemplate().equals(expectedTemplate))
				.findFirst()
				.orElseThrow();

		assertEquals(expectedTemplate, violation.getMessageTemplate());
	}
}