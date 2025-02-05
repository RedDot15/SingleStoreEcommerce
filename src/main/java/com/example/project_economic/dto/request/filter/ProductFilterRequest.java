package com.example.project_economic.dto.request.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest {
  Long categoryId;

  Double fromPrice;

  Double toPrice;

  Long colorId;

  Long sizeId;

  String keyword;
}
