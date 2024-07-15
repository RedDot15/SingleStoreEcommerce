package com.example.project_economic.impl;

import com.example.project_economic.entity.*;
import com.example.project_economic.mapper.CartItemMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.service.CartItemService;
import com.example.project_economic.utils.ProductUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartItemImpl implements CartItemService {
    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductWithAttributesRepository productWithAttributesRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private CartItemMapper cartItemMapper;

//    @Override
//    public List<CartItemResponse> listCartItem(Long userId) {
//        UserEntity user=this.userRepository.findById(userId).get();
//        List<CartItemEntity>cartItemEntities=this.cartItemRepository.findByUser(user);
//        List<CartItemResponse>cartItemResponses=cartItemEntities.stream()
//                .map(cartITemEntity->{
//                    CartItemResponse cartItemResponse=new CartItemResponse();
//                    cartItemResponse.setId(cartITemEntity.getId());
//                    cartItemResponse.setProductResponse(productUtils.enity_to_response(cartITemEntity.getProduct()));
//                    cartItemResponse.setQuantity(cartITemEntity.getQuantity());
//                    cartItemResponse.setUser(cartITemEntity.getUser());
//                    cartItemResponse.setSize(cartITemEntity.getSize());
//                    cartItemResponse.setColor(cartITemEntity.getColor());
//                    return cartItemResponse;
//                }).collect(Collectors.toList());
//
//        return cartItemResponses;
//    }

//    @Override
//    public CartItemEntity updateCard(Long cardId, Integer quantity) {
//        Optional<CartItemEntity> cartItemEntityP =this.cartItemRepository.findById(cardId);
//        if (cartItemEntityP.isPresent()){
//            CartItemEntity cartItemEntity = cartItemEntityP.get();
//            cartItemEntity.setQuantity(quantity);
//            return this.cartItemRepository.save(cartItemEntity);
//        }
//        return null;
//    }

    @Override
    public Set<CartItemResponse> getCartItemByUserId(Long userId) {
        Set<CartItemEntity> cartItemEntitySet = cartItemRepository.findByUserId(userId);

        return cartItemEntitySet.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toSet());
    }

    @Override
    public CartItemEntity addProduct(Long productId, Integer quantity, Long userId, Long sizeId, Long colorId) {
        //find user
        UserEntity userEntity = userRepository.findById(userId).get();

        //find product
        ProductEntity productEntity = productRepository.findById(productId).get();
        ColorEntity colorEntity = colorRepository.findById(colorId).get();
        SizeEntity sizeEntity = sizeRepository.findById(sizeId).get();
        ProductWithAttributesEntity productWithAttributesEntity = productWithAttributesRepository
                .findFirstByProductEntityAndColorEntityAndSizeEntity(productEntity,colorEntity,sizeEntity);

        CartItemEntity cartItemEntity= cartItemRepository.findByUserAndProductAttribute(userEntity.getId(),productWithAttributesEntity.getId());
        if(cartItemEntity==null){
            CartItemEntity cartItemEntity1 = new CartItemEntity();
            cartItemEntity1.setQuantity(quantity);
            cartItemEntity1.setProductWithAttributesEntity(productWithAttributesEntity);
            cartItemEntity1.setUserEntity(userEntity);
            return cartItemRepository.save(cartItemEntity1);
        }
        else{
            int quantityNew = cartItemEntity.getQuantity()+quantity;
            cartItemEntity.setQuantity(quantityNew);
        }
        return cartItemRepository.save(cartItemEntity);
    }

    @Override
    public void deleteCart(Long cartId) {
        this.cartItemRepository.deleteById(cartId);
    }

//    @Override
//    public Long countCart(Long userId) {
//        return this.cartItemRepository.countCart(userId);
//    }

//    @Override
//    public boolean findCartByProductId(Long productId,Long userId) {
//        Long cnt=this.cartItemRepository.countCartByProductIdAndUserId(productId,userId);
//        if(cnt==0)return false;
//        return true;
//    }
//
//    @Override
//    public void deleteAllCartByUserId(Long userId) {
////        UserEntity user=this.userRepository.findById(userId).get();
//        this.cartItemRepository.deleteAllCartByUserId(userId);
//    }
}
