package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	// Add
	CategoryEntity toCategoryEntity(CategoryRequest categoryRequest);

	// Update
	void updateCategoryEntityFromRequest(@MappingTarget CategoryEntity categoryEntity, CategoryRequest categoryRequest);

	// Response
	CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
}
