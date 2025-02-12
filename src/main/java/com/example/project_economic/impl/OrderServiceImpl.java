package com.example.project_economic.impl;

import com.example.project_economic.dto.request.OrderRequest;
import com.example.project_economic.dto.response.OrderResponse;
import com.example.project_economic.entity.OrderEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.OrderMapper;
import com.example.project_economic.repository.OrderRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
	OrderRepository orderRepository;
	UserRepository userRepository;
	OrderMapper orderMapper;

	@Override
	public OrderResponse add(OrderRequest orderRequest) {
		// Get Jwt token from Context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		// Get userId from token
		Long userId = jwt.getClaim("uid");
		// Save & Return new order
		return orderMapper.toOrderResponse(orderRepository.save(OrderEntity.builder()
				.id(orderRequest.getId())
				.totalAmount(orderRequest.getTotalAmount())
				.userEntity(userRepository.getReferenceById(userId))
				.build()));
	}

	@Override
	public OrderResponse update(OrderRequest orderRequest) {
		// Fetch
		OrderEntity orderEntity = orderRepository
				.findById(orderRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
		// Order not pending exception
		if (!orderEntity.getStatus().equals("Pending")) throw new AppException(ErrorCode.ORDER_COMPLETED);
		// Empty order exception
		if (orderEntity.getOrderItemEntityList().isEmpty()) throw new AppException(ErrorCode.ORDER_EMPTY);
		// Update status
		orderEntity.setStatus(orderRequest.getStatus());
		// Return
		return orderMapper.toOrderResponse(orderRepository.save(orderEntity));
	}
}
