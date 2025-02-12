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
public class PaymentCaptureResponse {
	String id;

	OrderResponse orderResponse;
}
