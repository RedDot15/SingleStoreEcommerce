package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ProductEntity;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface ProductMapper {
    ProductEntity toProductEntity(ProductRequest productRequest);
    ProductResponse toProductResponse(ProductEntity productEntity);
}
