package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import java.util.Set;

public interface ProductDetailService {
  // Fetch
  Set<ProductDetailResponse> getAllByProductId(Long productId);

  // Add/Update/Delete
  ProductDetailResponse add(ProductDetailRequest productDetailRequest);

  ProductDetailResponse update(ProductDetailRequest productDetailRequest);

  Long delete(Long id);

  // Change status
  ProductDetailResponse activate(Long id);

  String getDeactivateCheckMessage(Long id);

  ProductDetailResponse deactivate(Long id);
}
