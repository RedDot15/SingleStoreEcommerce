package com.example.project_economic.service;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;

import java.util.Set;

public interface ColorService {
    // Fetch
    Set<ColorResponse> getAll();

    Set<ColorResponse> getAllByProductId(Long productId);

    Set<ColorResponse> getActiveByProductId(Long productId);

    // Add/Update/Delete
    ColorResponse add(ColorRequest colorRequest);

    ColorResponse update(ColorRequest colorRequest);

    Long delete(Long id);

}
