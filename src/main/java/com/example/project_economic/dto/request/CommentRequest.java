package com.example.project_economic.dto.request;

import com.example.project_economic.validation_group.Create;
import com.example.project_economic.validation_group.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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

    @NotNull(groups = {Create.class,Update.class}, message = "Content is required.")
    @Size(max = 255, message = "Content must not exceed 255 characters.")
    String content;

    @NotNull(groups = {Create.class,Update.class}, message = "Star is required.")
    Integer star;

    @NotNull(groups = Create.class, message = "User ID is required.")
    Long userId;

    @NotNull(groups = Create.class, message = "Product ID is required.")
    Long productId;

}
