package com.example.project_economic.service;

import com.example.project_economic.dto.request.OrderRequest;
import com.example.project_economic.dto.response.OrderResponse;

public interface OrderService {
	// Add
	OrderResponse add(OrderRequest orderRequest);

	// Update
	OrderResponse update(OrderRequest orderRequest);
}
