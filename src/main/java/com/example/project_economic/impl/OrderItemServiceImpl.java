package com.example.project_economic.impl;

import com.example.project_economic.dto.request.OrderItemRequest;
import com.example.project_economic.dto.response.*;
import com.example.project_economic.entity.*;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.*;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.OrderItemService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class OrderItemServiceImpl implements OrderItemService {
	OrderItemRepository orderItemRepository;
	CartItemRepository cartItemRepository;
	ProductRepository productRepository;
	ProductDetailRepository productDetailRepository;
	SizeRepository sizeRepository;
	ColorRepository colorRepository;
	CategoryRepository categoryRepository;
	OrderRepository orderRepository;
	UserRepository userRepository;
	CategoryMapper categoryMapper;
	ProductMapper productMapper;
	ProductDetailMapper productDetailMapper;
	ColorMapper colorMapper;
	SizeMapper sizeMapper;
	OrderItemMapper orderItemMapper;

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('GET_ALL_ORDER_ITEM') or (#userId == authentication.principal.claims['uid'])")
	@Override
	public List<OrderItemResponse> getAllByUserId(Long userId) {
		// Fetch order-item list
		List<OrderItemEntity> orderItemEntityList = orderItemRepository.findAllByUserId(userId);
		// Declare response list
		List<OrderItemResponse> orderItemResponseList = new ArrayList<>();
		// For each to get related Entity (including deleted)
		for (OrderItemEntity orderItemEntity : orderItemEntityList) {
			// Fetch
			ProductDetailEntity foundProductDetailEntity =
					productDetailRepository.findIncludingDeletedById(orderItemEntity.getProductDetailId());
			ProductEntity foundProductEntity =
					productRepository.findIncludingDeletedById(foundProductDetailEntity.getProductId());
			ColorEntity foundColorEntity =
					colorRepository.findIncludingDeletedById(foundProductDetailEntity.getColorId());
			SizeEntity foundSizeEntity = sizeRepository.findIncludingDeletedById(foundProductDetailEntity.getSizeId());
			CategoryEntity foundCategoryEntity =
					categoryRepository.findIncludingDeletedById(foundProductEntity.getCategoryId());
			// Mapping
			ProductDetailResponse productDetailResponse =
					productDetailMapper.toProductDetailResponse(foundProductDetailEntity);
			ProductResponse productResponse = productMapper.toProductResponse(foundProductEntity);
			ColorResponse colorResponse = colorMapper.toColorResponse(foundColorEntity);
			SizeResponse sizeResponse = sizeMapper.toSizeResponse(foundSizeEntity);
			CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(foundCategoryEntity);
			// Set
			productResponse.setCategoryResponse(categoryResponse);
			productDetailResponse.setProductResponse(productResponse);
			productDetailResponse.setColorResponse(colorResponse);
			productDetailResponse.setSizeResponse(sizeResponse);
			// Response
			OrderItemResponse orderItemResponse = orderItemMapper.toOrderItemResponse(orderItemEntity);
			orderItemResponse.setProductDetailResponse(productDetailResponse);
			// Add to list
			orderItemResponseList.add(orderItemResponse);
		}
		// Return
		return orderItemResponseList;
	}

	@PreAuthorize("@securityService.isOrderOwner(#orderItemRequest.orderId, authentication.principal.claims['uid'])")
	@Override
	public List<OrderItemResponse> addMyItem(OrderItemRequest orderItemRequest) {
		// Get Jwt token from Context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		// Get userId from token
		Long userId = jwt.getClaim("uid");
		// Fetch
		List<CartItemEntity> cartItemEntityList = cartItemRepository.findAllByUserIdAndProductDetailIdIn(
				userId, orderItemRequest.getProductDetailIdList());
		OrderEntity orderEntity = orderRepository
				.findById(orderItemRequest.getOrderId())
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
		// Count total amount & total amount mismatch exception
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (CartItemEntity cartItemEntity : cartItemEntityList) {
			Integer quantity = cartItemEntity.getQuantity();
			BigDecimal salePrice =
					cartItemEntity.getProductDetailEntity().getProductEntity().getSalePrice();
			totalAmount = totalAmount.add(salePrice.multiply(new BigDecimal(quantity)));
		}
		if (!totalAmount.equals(orderEntity.getTotalAmount())) throw new AppException(ErrorCode.ORDER_PRICE_MISMATCH);
		// Create order-item list for response
		List<OrderItemResponse> orderItemResponseList = new LinkedList<>();
		// For each cart-item entity list to add new order-item
		for (CartItemEntity cartItemEntity : cartItemEntityList) {
			// Create & Save new order-item, then mapping to response
			OrderItemResponse orderItemResponse =
					orderItemMapper.toOrderItemResponse(orderItemRepository.save(OrderItemEntity.builder()
							.orderEntity(orderEntity)
							.productDetailEntity(cartItemEntity.getProductDetailEntity())
							.quantity(cartItemEntity.getQuantity())
							.build()));
			// Add to response list
			orderItemResponseList.add(orderItemResponse);
		}
		// Return result
		return orderItemResponseList;
	}
}
