package com.indra.retail.orders.dto;

import com.indra.retail.orders.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
	

	@NotBlank(message = "{orders.customerId.notBlank}")
	String customerId,
	
	@NotEmpty(message = "{orders.items.notEmpty}")
	List<OrderItem> items,
	
	@NotBlank(message = "{orders.deliveryAddress.notBlank}")
	@Size(min = 10, message = "{orders.deliveryAddress.size}")
	String deliveryAddress) 
	{
	}
