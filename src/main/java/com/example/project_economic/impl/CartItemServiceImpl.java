package com.example.project_economic.impl;

import com.example.project_economic.dto.request.CartItemRequest;
import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.CartItemMapper;
import com.example.project_economic.repository.CartItemRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.CartItemService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service
public class CartItemServiceImpl implements CartItemService {
	CartItemRepository cartItemRepository;
	UserRepository userRepository;
	ProductDetailRepository productDetailRepository;
	CartItemMapper cartItemMapper;

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('GET_ALL_CART_ITEM') or (#userId == authentication.principal.claims['uid'])")
	@Override
	public List<CartItemResponse> getAllByUserId(Long userId) {
		// Return result
		return cartItemRepository.findAllByUserId(userId).stream()
				.map(cartItemMapper::toCartItemResponse)
				.collect(Collectors.toList());
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('GET_ALL_CART_ITEM') or (#userId == authentication.principal.claims['uid'])")
	@Override
	public Long countAllByUserId(Long userId) {
		// Return cart's size by user ID
		return cartItemRepository.countAllByUserId(userId);
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('ADD_CART_ITEM') or (#cartItemRequest.userId == authentication.principal.claims['uid'])")
	@Override
	public CartItemResponse add(CartItemRequest cartItemRequest) {
		// Checking if a matching cart-item exists
		CartItemEntity cartItemEntity = cartItemRepository.findFirstByUserIdAndProductDetailId(
				cartItemRequest.getUserId(), cartItemRequest.getProductDetailId());
		// If exist: Update cart-item
		if (cartItemEntity != null) {
			// Calculate up new quantity
			cartItemEntity.setQuantity(cartItemEntity.getQuantity() + cartItemRequest.getQuantity());
			// Return result
			return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItemEntity));
		}
		// Create new cart-item
		CartItemEntity newCartItemEntity = CartItemEntity.builder()
				.userEntity(userRepository.getReferenceById(cartItemRequest.getUserId()))
				.productDetailEntity(productDetailRepository.getReferenceById(cartItemRequest.getProductDetailId()))
				.quantity(cartItemRequest.getQuantity())
				.build();
		// Save and Return
		return cartItemMapper.toCartItemResponse(cartItemRepository.save(newCartItemEntity));
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('UPDATE_CART_ITEM') or @securityService.isCartItemOwner(#cartItemRequest.id, authentication.principal.claims['uid'])")
	@Override
	public CartItemResponse update(CartItemRequest cartItemRequest) {
		// Get old & Not found/Deleted exception
		CartItemEntity oldCartItemEntity = cartItemRepository
				.findById(cartItemRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
		// Update
		oldCartItemEntity.setQuantity(cartItemRequest.getQuantity());
		// Save & Return
		return cartItemMapper.toCartItemResponse(cartItemRepository.save(oldCartItemEntity));
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('DELETE_CART_ITEM') or @securityService.isCartItemOwner(#id, authentication.principal.claims['uid'])")
	@Override
	public Long delete(Long id) {
		// Retrieve the cart item to check ownership
		CartItemEntity cartItemEntity =
				cartItemRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
		// Delete cart-item
		cartItemRepository.deleteById(id);
		// Return id
		return id;
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('DELETE_CART_ITEM') or (#userId == authentication.principal.claims['uid'])")
	@Override
	public Long deleteAllByUserId(Long userId) {
		// Delete cart-item
		cartItemRepository.deleteAllByUserId(userId);
		// Return id
		return userId;
	}
}
