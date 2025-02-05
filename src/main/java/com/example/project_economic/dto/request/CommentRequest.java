package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
  @Null(groups = Create.class, message = "Id must not be provided during create.")
  @NotNull(groups = Update.class, message = "Id is required during update.")
  Long id;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Content is required.")
  @Size(max = 255, message = "Content must not exceed {max} characters.")
  String content;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Star is required.")
  @Positive(message = "Star must be greater than 0.")
  Integer star;

  @NotNull(groups = Create.class, message = "User ID is required.")
  Long userId;

  @NotNull(groups = Create.class, message = "Product ID is required.")
  Long productId;
}
