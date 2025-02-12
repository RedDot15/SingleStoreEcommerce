package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.OrderResponse;
import com.example.project_economic.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	// Response
	OrderResponse toOrderResponse(OrderEntity orderEntity);
}
