package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.entity.CartItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);
}
