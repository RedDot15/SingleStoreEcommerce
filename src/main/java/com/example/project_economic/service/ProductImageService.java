package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductImageResponse;

import java.util.Set;

public interface ProductImageService {
    Set<ProductImageResponse> getAllByProductId(Long productId);

    Boolean existsByProductIdAndColorId(Long productId, Long colorId);

    ProductImageResponse getFirstById(Long id);

    ProductImageResponse create(ProductImageRequest productImageRequest);

    ProductImageResponse update(ProductImageRequest productImageRequest);

    void delete(Long id);

    ProductImageResponse activate(Long id);

    String getDeactivateCheckMessage(Long id);

    ProductImageResponse deactivate(Long id);
}
