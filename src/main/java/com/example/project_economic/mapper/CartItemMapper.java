package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "productDetailResponse", source = "productDetailEntity")
    @Mapping(target = "userResponse", source = "userEntity")
    CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);
    @Mapping(target = "productResponse", source = "productEntity")
    ProductDetailResponse toProductDetailResponse(ProductDetailEntity productDetailEntity);
    @Mapping(target = "activeProductImageResponseSet", source = "activeProductImageEntitySet")
    ProductResponse toProductResponse(ProductEntity productEntity);
}
