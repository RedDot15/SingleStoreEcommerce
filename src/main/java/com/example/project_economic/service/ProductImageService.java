package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductImageResponse;

import java.util.Set;

public interface ProductImageService {
    // Fetch
    Set<ProductImageResponse> getAllByProductId(Long productId);

    ProductImageResponse getFirstActiveByProductIdAndColorId(Long productId, Long colorId);

    // Create/Update/Delete
    ProductImageResponse add(ProductImageRequest productImageRequest);

    ProductImageResponse update(ProductImageRequest productImageRequest);

    Long delete(Long id);

    // Change status
    ProductImageResponse activate(Long id);

    String getDeactivateCheckMessage(Long id);

    ProductImageResponse deactivate(Long id);
}
