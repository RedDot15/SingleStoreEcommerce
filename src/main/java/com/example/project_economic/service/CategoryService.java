package com.example.project_economic.service;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;

import java.util.Set;

public interface CategoryService {
    Set<CategoryResponse> getAllForAdmin();

    Set<CategoryResponse> getAllActive();

    Boolean existsById(Long id);

    Boolean existsByName(String name);

    Boolean existsByNameExceptId(String name, Long id);

    CategoryResponse getFirstById(Long id);

    CategoryResponse create(CategoryRequest categoryRequest);

    CategoryResponse update(CategoryRequest categoryRequest);

    void delete(Long id);

    Boolean activateCheck(Long id);

    CategoryResponse activate(Long id);

    CategoryResponse deactivate(Long id);
}
