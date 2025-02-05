package com.example.project_economic.service;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import java.util.Set;

public interface CategoryService {
  // Fetch
  Set<CategoryResponse> getAll();

  Set<CategoryResponse> getActive();

  // Add/Update/Delete
  CategoryResponse add(CategoryRequest categoryRequest);

  CategoryResponse update(CategoryRequest categoryRequest);

  Long delete(Long id);

  // Change status
  CategoryResponse activate(Long id);

  CategoryResponse deactivate(Long id);

  // Validate
  CategoryEntity validateCategoryIsActive(Long id);
}
