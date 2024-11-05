package com.example.project_economic.service;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.awt.*;
import java.util.List;
import java.util.Set;

public interface ColorService {
    Set<ColorResponse> getAll();

    Boolean existsById(Long id);

    Boolean existsByNameOrHexCode(String name, String hexCode);

    Boolean existsByNameOrHexCodeExceptId(String name, String hexCode, Long id);

    ColorResponse create(ColorRequest colorRequest);

    ColorResponse update(ColorRequest colorRequest);

    void delete(Long id);

    Set<ColorResponse> getAllColorOfAProduct(Long productId);
}
