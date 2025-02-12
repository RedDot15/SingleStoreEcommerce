package com.example.project_economic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
	@NotBlank(message = "Order Id is required.")
	String orderId;

	@NotEmpty(message = "Product detail Id list is required.")
	List<Long> productDetailIdList;
}
