package com.example.project_economic.dto.response;

import com.example.project_economic.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;
    Long costPrice;
    Long salePrice;
    Integer likes;
    Boolean isActive;

//    CategoryEntity categoryEntity;
//    Set<ColorEntity> colorEntitySet;
//    Set<SizeEntity> sizeEntitySet;
    @JsonIgnore
    List<ProductImageEntity> productImageEntityList;

    public String getSalePriceFormat(){
        try{
            String formattedNumber = this.salePrice.toString();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < formattedNumber.length(); i++) {
                char currentChar = formattedNumber.charAt(i);
                if (Character.isDigit(currentChar) && i > 0 && (formattedNumber.length() - i) % 3 == 0) result.append('.');
                result.append(currentChar);
            }
            return result.toString() + '₫';
        }
        catch(Exception e){
            return "123";
        }
    }

    public String getCostPriceFormat(){
        try{
            String formattedNumber = this.costPrice.toString();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < formattedNumber.length(); i++) {
                char currentChar = formattedNumber.charAt(i);
                if (Character.isDigit(currentChar) && i > 0 && (formattedNumber.length() - i) % 3 == 0) result.append('.');
                result.append(currentChar);
            }
            return result.toString() + '₫';
        }
        catch (Exception e){
            return "1234";
        }

    }
}
