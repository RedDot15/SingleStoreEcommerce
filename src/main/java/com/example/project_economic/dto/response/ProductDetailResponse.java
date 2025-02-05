package com.example.project_economic.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse implements Comparable {
  Long id;

  ProductResponse productResponse;

  ColorResponse colorResponse;

  SizeResponse sizeResponse;

  Integer stock;

  Boolean isActive;

  @JsonIgnore Boolean isNew;

  @Override
  public int compareTo(Object o) {
    return id.compareTo(((ProductDetailResponse) o).getId());
  }
}
