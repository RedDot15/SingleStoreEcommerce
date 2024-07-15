package com.example.project_economic.service;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();

    void save(CategoryRequest categoryRequest);

    CategoryEntity findById(Long id);

    void deleteById(Long categoryId);

//    void enableById(Long id);

    List<CategoryEntity>findAllByActived();
    //customer
    List<CategoryRequest>getCategoryAndProduct();
}
