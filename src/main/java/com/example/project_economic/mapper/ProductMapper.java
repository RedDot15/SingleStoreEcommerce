package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity toProductEntity(ProductRequest productRequest);
    ProductResponse toProductResponse(ProductEntity productEntity);
}
