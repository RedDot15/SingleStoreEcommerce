package com.example.project_economic.service;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;

import java.util.List;

public interface ColorService {
    List<ColorResponse> getAll();

    void save(ColorRequest colorRequest);

    void deleteById(Long colorId);

    List<ColorResponse> getAllColorOfAProduct(Long productId);
}
