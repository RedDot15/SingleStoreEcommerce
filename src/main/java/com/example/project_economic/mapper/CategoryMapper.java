package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toCategoryEntity(CategoryRequest categoryRequest);
    CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
}
