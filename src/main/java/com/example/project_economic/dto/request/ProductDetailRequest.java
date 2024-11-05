package com.example.project_economic.dto.request;

import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.dto.response.SizeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailRequest {
    Long id;
    Long productId;
    Long colorId;
    Long sizeId;
    Integer stock;
}
