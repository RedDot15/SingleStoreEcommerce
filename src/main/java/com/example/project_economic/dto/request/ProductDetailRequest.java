package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailRequest {
    @Null(groups = Create.class, message = "Id must not be provided during creation.")
    @NotNull(groups = Update.class, message = "Id is required during update.")
    Long id;

    @NotNull(groups = Create.class, message = "Product ID is required.")
    @Positive(message = "Product ID must be greater than 0.")
    Long productId;

    @NotNull(groups = Create.class, message = "Color ID is required.")
    @Positive(message = "Color ID must be greater than 0.")
    Long colorId;

    @NotNull(groups = Create.class, message = "Size ID is required.")
    @Positive(message = "Size ID must be greater than 0.")
    Long sizeId;

    @NotNull(groups = {Create.class,Update.class}, message = "Stock is required.")
    @PositiveOrZero(message = "Stock must be 0 or greater.")
    Integer stock;
}
