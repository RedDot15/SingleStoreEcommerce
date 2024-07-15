package com.example.project_economic.service;


import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.dto.response.CartItemResponse;

import java.util.List;
import java.util.Set;

public interface CartItemService {
    public Set<CartItemResponse> getCartItemByUserId(Long userId);

//    public CartItemEntity updateCard(Long cardId,Integer quantity);
    public CartItemEntity addProduct(Long productId, Integer quantity, Long userId, Long sizeId, Long colorId);

    public void deleteCart(Long cartId);
//    Long countCart(Long userId);
//
//    public boolean findCartByProductId(Long productId,Long userId);
//
//    void deleteAllCartByUserId(Long userId);
}
