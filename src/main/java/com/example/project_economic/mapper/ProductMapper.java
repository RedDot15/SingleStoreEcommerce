package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    // Add
    ProductEntity toProductEntity(ProductRequest productRequest);
    // Update
    void updateProductEntityFromRequest(@MappingTarget ProductEntity productEntity, ProductRequest productRequest);
    // Response
    @Mapping(target = "categoryResponse", source = "categoryEntity")
    @Mapping(target = "activeProductImageResponseSet", source = "activeProductImageEntitySet")
    ProductResponse toProductResponse(ProductEntity productEntity);
}
