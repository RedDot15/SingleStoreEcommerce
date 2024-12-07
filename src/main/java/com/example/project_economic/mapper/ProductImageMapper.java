package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.ProductImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    // Response
    ProductImageResponse toProductImageResponse(ProductImageEntity productImageEntity);
}
