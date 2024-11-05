package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.entity.ProductDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    //ProductDetailMapper
    @Mapping(target = "productResponse", source = "productEntity")
    @Mapping(target = "colorResponse", source = "colorEntity")
    @Mapping(target = "sizeResponse", source = "sizeEntity")
    ProductDetailResponse toProductDetailResponse(ProductDetailEntity productDetailEntity);

    ProductDetailEntity toProductDetailEntity(ProductDetailRequest productDetailRequest);
}
