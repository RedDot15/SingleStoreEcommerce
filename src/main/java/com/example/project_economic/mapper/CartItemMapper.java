package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
	// Response
	@Mapping(target = "productDetailResponse", source = "productDetailEntity")
	CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);

	@Mapping(target = "productResponse", source = "productEntity")
	@Mapping(target = "colorResponse", source = "colorEntity")
	@Mapping(target = "sizeResponse", source = "sizeEntity")
	ProductDetailResponse toProductDetailResponse(ProductDetailEntity productDetailEntity);

	@Mapping(target = "categoryResponse", source = "categoryEntity")
	@Mapping(target = "activeProductImageResponseSet", source = "activeProductImageEntitySet")
	ProductResponse toProductResponse(ProductEntity productEntity);
}
