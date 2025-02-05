package com.example.project_economic.dto.response;

import com.example.project_economic.utils.CurrencyFormatter;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
	Long id;

	ProductDetailResponse productDetailResponse;

	Integer quantity;

	public String totalMoney() {
		return CurrencyFormatter.getFormattedCurrency(productDetailResponse
				.getProductResponse()
				.getSalePrice()
				.multiply(new BigDecimal(quantity))
				.toString());
	}
}
