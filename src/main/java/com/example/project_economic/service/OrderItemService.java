package com.example.project_economic.service;

import com.example.project_economic.dto.response.OrderItemResponse;

import java.util.List;

public interface OrderItemService {
    // Fetch
    List<OrderItemResponse> getAllByUserId(Long userId);

    // Add
    List<OrderItemResponse> addMyItem();
}
