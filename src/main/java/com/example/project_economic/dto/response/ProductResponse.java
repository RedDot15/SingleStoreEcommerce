package com.example.project_economic.dto.response;

import com.example.project_economic.utils.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse implements Comparable {
  Long id;

  String name;

  String description;

  BigDecimal costPrice;

  BigDecimal salePrice;

  Integer likes;

  Boolean isActive;

  CategoryResponse categoryResponse;

  Set<ProductImageResponse> activeProductImageResponseSet;

  public String getFormattedSalePrice() {
    return CurrencyFormatter.getFormattedCurrency(salePrice.toString());
  }

  public String getFormattedCostPrice() {
    return CurrencyFormatter.getFormattedCurrency(costPrice.toString());
  }

  @Override
  public int compareTo(Object o) {
    return id.compareTo(((ProductResponse) o).getId());
  }
}
