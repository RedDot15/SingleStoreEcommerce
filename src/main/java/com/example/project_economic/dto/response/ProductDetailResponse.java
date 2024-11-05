package com.example.project_economic.dto.response;

import com.example.project_economic.entity.SizeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse implements Comparable{
    Long id;
    ProductResponse productResponse;
    ColorResponse colorResponse;
    SizeResponse sizeResponse;
    Integer stock;
    Boolean isActive;

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((ProductDetailResponse)o).getId());
    }
}
