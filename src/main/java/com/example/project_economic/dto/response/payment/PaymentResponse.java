package com.example.project_economic.dto.response.payment;

import com.example.project_economic.dto.response.OrderResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
	OrderResponse orderResponse;

	String redirectUrl;
}
