package com.example.project_economic.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
	@NotNull(message = "Fee is required.")
	BigDecimal fee;

	@NotBlank(message = "Currency is required.")
	String currency;

	@NotBlank(message = "Return Url is required.")
	String returnUrl;

	@NotBlank(message = "Success Url is required.")
	String successUrl;
}
