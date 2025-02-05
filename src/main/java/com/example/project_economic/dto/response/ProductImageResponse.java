package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageResponse implements Comparable {
  Long id;

  String name;

  Boolean isActive;

  @Override
  public int compareTo(Object o) {
    return id.compareTo(((ProductImageResponse) o).getId());
  }
}
