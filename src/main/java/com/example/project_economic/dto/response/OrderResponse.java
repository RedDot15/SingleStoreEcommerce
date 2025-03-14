package com.example.project_economic.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
	String id;

	BigDecimal totalAmount;

	LocalDateTime boughtAt;

	Boolean received;

	String status;
}
