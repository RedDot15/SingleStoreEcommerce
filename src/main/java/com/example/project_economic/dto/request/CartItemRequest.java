package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {
    @Null(groups = Create.class, message = "Id must not be provided during addition.")
    @NotNull(groups = Update.class, message = "Id is required during update.")
    Long id;

    @NotNull(groups = Create.class, message = "User ID is required.")
    Long userId;

    @NotNull(groups = Create.class, message = "Product detail ID is required.")
    Long productDetailId;

    @NotNull(groups = {Create.class,Update.class}, message = "Quantity is required.")
    @Positive(message = "Quantity must be a positive number.")
    Integer quantity;
}
