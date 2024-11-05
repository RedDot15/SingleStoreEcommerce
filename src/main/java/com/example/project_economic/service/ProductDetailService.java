package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;

import java.util.Set;

public interface ProductDetailService {
    Set<ProductDetailResponse> getAllByProductId(Long productId);

    ProductDetailResponse getFirstById(Long id);

    ProductDetailResponse getFirstByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId);

    ProductDetailResponse create(ProductDetailRequest productDetailRequest);

    ProductDetailResponse update(ProductDetailRequest productDetailRequest);

    void delete(Long id);

    ProductDetailResponse calculateUp(ProductDetailRequest productDetailRequest);

    ProductDetailResponse activate(Long id);

    String getDeactivateCheckMessage(Long id);

    ProductDetailResponse deactivate(Long id);
}
