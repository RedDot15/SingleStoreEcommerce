package com.example.project_economic.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductDetailResponse productDetailResponse;
    @JsonIgnore
    private UserResponse userResponse;
    private int quantity;
//    public String totalInCartItem(){
//        String formattedNumber = String.valueOf((this.quantity*this.productResponse.getSalePrice()));
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < formattedNumber.length(); i++) {
//            char currentChar = formattedNumber.charAt(i);
//            if (Character.isDigit(currentChar) && i > 0 && (formattedNumber.length() - i) % 3 == 0) result.append('.');
//            result.append(currentChar);
//        }
//        System.out.println(result.toString());
//        return result.toString();
//    }

    public Long totalInCartItem(){
        return quantity * productDetailResponse.getProductResponse().getSalePrice();
    }
}
