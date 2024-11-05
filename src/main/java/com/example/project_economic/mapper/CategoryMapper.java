package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    //Create
    CategoryEntity toCategoryEntity(CategoryRequest categoryRequest);

    //Update
    void updateCategoryEntityFromRequest(@MappingTarget CategoryEntity categoryEntity, CategoryRequest categoryRequest);

    //Result
    @Mapping(target = "activeProductResponseSet", source = "activeProductEntitySet")
    CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);

    CategoryResponse toSimpleCategoryResponse(CategoryEntity categoryEntity);
}
