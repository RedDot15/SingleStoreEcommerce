package com.example.project_economic.dto.request;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
	String id;

	BigDecimal totalAmount;

	String status;
}
