package com.example.project_economic.service;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import java.util.Set;

public interface SizeService {
	// Fetch
	Set<SizeResponse> getAll();

	Set<SizeResponse> getActiveByProductId(Long productId);

	// Add/Update/Delete
	SizeResponse add(SizeRequest sizeRequest);

	SizeResponse update(SizeRequest sizeRequest);

	Long delete(Long sizeId);
}
