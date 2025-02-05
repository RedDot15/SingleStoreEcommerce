package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
  @Null(groups = Create.class, message = "Id must not be provided during creation.")
  @NotNull(groups = Update.class, message = "Id is required during update.")
  Long id;

  @NotBlank(
      groups = {Create.class, Update.class},
      message = "Product name is required.")
  @Size(max = 100, message = "Product name must not exceed {max} characters.")
  String name;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Description is required.")
  @Size(max = 500, message = "Description must not exceed {max} characters.")
  String description;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Cost price is required.")
  @Min(value = 0, message = "Cost price must be a positive value.")
  Long costPrice;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Sale price is required.")
  @Min(value = 0, message = "Sale price must be a positive value.")
  Long salePrice;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Category ID is required.")
  @Positive(message = "Category ID must be a positive number.")
  Long categoryId;
}
