package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.CartItemRequest;
import com.example.project_economic.entity.*;
import com.example.project_economic.mapper.CartItemMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.service.CartItemService;
import com.example.project_economic.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class CartItemServiceImpl implements CartItemService {
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ProductDetailRepository productDetailRepository;
    CartItemMapper cartItemMapper;

    @Override
    public List<CartItemResponse> getAllByUserId(Long userId) {
        // Return result
        return cartItemRepository.findAllByUserId(userId)
                .stream().map(cartItemMapper::toCartItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countAllByUserId(Long userId) {
        // Return cart's size by user ID
        return cartItemRepository.countAllByUserId(userId);
    }

    @Override
    public CartItemResponse add(CartItemRequest cartItemRequest) {
        // Checking if a matching cart-item exists
        CartItemEntity cartItemEntity = cartItemRepository.findFirstByUserIdAndProductDetailId(
                cartItemRequest.getUserId(),
                cartItemRequest.getProductDetailId()
        );
        // If exist: Update cart-item
        if (cartItemEntity != null){
            // Calculate up new quantity
            cartItemEntity.setQuantity(cartItemEntity.getQuantity() + cartItemRequest.getQuantity());
            // Return result
            return cartItemMapper.toCartItemResponse(
                    cartItemRepository.save(cartItemEntity)
            );
        }
        // Create new cart-item
        CartItemEntity newCartItemEntity = CartItemEntity.builder()
                .userEntity(userRepository.getReferenceById(cartItemRequest.getUserId()))
                .productDetailEntity(productDetailRepository.getReferenceById(cartItemRequest.getProductDetailId()))
                .quantity(cartItemRequest.getQuantity())
                .build();
        // Save and Return
        return cartItemMapper.toCartItemResponse(
                cartItemRepository.save(newCartItemEntity)
        );
    }

    @Override
    public CartItemResponse update(CartItemRequest cartItemRequest) {
        // Get old & Not found/Deleted exception
        CartItemEntity oldCartItemEntity = cartItemRepository.findById(cartItemRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart-item to update not found"));
        // Update
        oldCartItemEntity.setQuantity(cartItemRequest.getQuantity());
        // Save & Return
        return cartItemMapper.toCartItemResponse(
                cartItemRepository.save(oldCartItemEntity)
        );
    }

    @Override
    public Long delete(Long id) {
        // Delete cart-item
        cartItemRepository.deleteById(id);
        // Return id
        return id;
    }

    @Override
    public Long deleteAllByUserId(Long id) {
        // Delete cart-item
        cartItemRepository.deleteAllByUserId(id);
        // Return id
        return id;
    }
}
