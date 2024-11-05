package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    //Create
    ProductEntity toProductEntity(ProductRequest productRequest);
    //Update
    void updateProductEntityFromRequest(@MappingTarget ProductEntity productEntity, ProductRequest productRequest);
    //Result
    @Mapping(target = "categoryResponse", source = "categoryEntity")
    @Mapping(target = "activeProductImageResponseSet", source = "activeProductImageEntitySet")
    @Mapping(target = "activeProductDetailResponseSet", source = "activeProductDetailEntitySet")
    ProductResponse toProductResponse(ProductEntity productEntity);
    @Mapping(target = "colorResponse", source = "colorEntity")
    @Mapping(target = "sizeResponse", source = "sizeEntity")
    ProductDetailResponse toProductDetailResponse(ProductDetailEntity productDetailEntity);

    @Mapping(target = "categoryResponse", source = "categoryEntity")
    @Mapping(target = "activeProductImageResponseSet", source = "activeProductImageEntitySet")
    ProductResponse toProductAdminResponse(ProductEntity productEntity);

}
