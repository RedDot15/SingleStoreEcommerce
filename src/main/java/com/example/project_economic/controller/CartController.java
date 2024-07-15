package com.example.project_economic.controller;

import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.service.CartItemService;
import com.example.project_economic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/shoppingcarts")
public class CartController {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UserService userService;

//    @PostMapping("/update/{cardId}/{quantity}")
//    public String updateCard(@PathVariable Long cardId
//            , @PathVariable Integer quantity){
//        CartItemEntity cartItemEntity= this.cartItemService.updateCard(cardId,quantity);
////        Long total=cartItemEntity.totalInCartItem();
//        return String.valueOf(total);
//    }
    @DeleteMapping("/delete/{cartId}")
    public String deleteCart(@PathVariable Long cartId){
        this.cartItemService.deleteCart(cartId);
        return "delete success "+cartId;
    }
//    @PostMapping("/add/{productId}/{quantity}/{userId}")
//    public Long addProductToCart(
//            @PathVariable Long productId,
//            @PathVariable Integer quantity,
//            @PathVariable Long userId
//    ){
//        CartItemEntity cartItemEntity=this.cartItemService.addProduct(productId,quantity,userId, "", "");
//        List<CartItemResponse>cartItemEntities=this.cartItemService.listCartItem(userId);
//        return this.cartItemService.countCart(userId);
//    }
    @PostMapping("/add1/{productId}/{quantity}/{userId}/{sizeId}/{colorId}")
    public Long addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity,
            @PathVariable Long userId,
            @PathVariable Long sizeId,
            @PathVariable Long colorId
    ){
        CartItemEntity cartItemEntity=this.cartItemService.addProduct(productId,quantity,userId, sizeId, colorId);
//        List<CartItemResponse>cartItemEntities=this.cartItemService.listCartItem(userId);
//        model.addAttribute("cartItems",cartItemEntities);
//        return this.cartItemService.countCart(userId);
        return 0L;
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllCartItemByUserId(@PathVariable Long userId){
        return new ResponseEntity<>(cartItemService.getCartItemByUserId(userId), HttpStatus.OK);
    }
}
