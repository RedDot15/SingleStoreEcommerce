package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductImageResponse;
import java.util.Set;

public interface ProductImageService {
  // Fetch
  Set<ProductImageResponse> getAllByProductId(Long productId);

  ProductImageResponse getActiveByProductIdAndColorId(Long productId, Long colorId);

  // Add/Update/Delete
  ProductImageResponse add(ProductImageRequest productImageRequest);

  ProductImageResponse update(ProductImageRequest productImageRequest);

  Long delete(Long id);

  // Change status
  ProductImageResponse activate(Long id);

  String getDeactivateCheckMessage(Long id);

  ProductImageResponse deactivate(Long id);
}
