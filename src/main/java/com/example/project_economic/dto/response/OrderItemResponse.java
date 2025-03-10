package com.example.project_economic.dto.response;

import com.example.project_economic.utils.CurrencyFormatter;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
	Long id;

	OrderResponse orderResponse;

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
