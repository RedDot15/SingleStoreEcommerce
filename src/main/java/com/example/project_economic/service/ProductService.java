package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.request.filter.ProductFilterRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ProductEntity;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface ProductService {
	// Fetch
	Set<ProductResponse> getAll();

	Set<ProductResponse> getActiveByCategoryId(Long categoryId);

	Page<ProductResponse> getActiveByFilter(
			Integer pageNumber, Integer pageSize, ProductFilterRequest productFilterRequest);

	ProductResponse getActiveById(Long id);

	// Add/Update/Delete
	ProductResponse add(ProductRequest productRequest);

	ProductResponse update(ProductRequest productRequest);

	Long delete(Long id);

	// Change status
	ProductResponse activate(Long id);

	String getDeactivateCheckMessage(Long id);

	ProductResponse deactivate(Long id);

	// Update number of like
	ProductResponse like(Long id);

	// Validate
	ProductEntity validateProductIsActive(Long id);
}
