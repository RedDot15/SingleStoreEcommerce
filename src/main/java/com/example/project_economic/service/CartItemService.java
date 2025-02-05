package com.example.project_economic.service;

import com.example.project_economic.dto.request.CartItemRequest;
import com.example.project_economic.dto.response.CartItemResponse;
import java.util.List;

public interface CartItemService {
  // Fetch
  List<CartItemResponse> getAllByUserId(Long userId);

  // Count
  Long countAllByUserId(Long userId);

  // Add/Update/Delete
  CartItemResponse add(CartItemRequest cartItemRequest);

  CartItemResponse update(CartItemRequest cartItemRequest);

  Long delete(Long id);

  // Delete all by ID
  Long deleteAllByUserId(Long id);
}
