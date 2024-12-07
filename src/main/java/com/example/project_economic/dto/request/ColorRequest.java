package com.example.project_economic.dto.request;

import com.example.project_economic.validation_group.Create;
import com.example.project_economic.validation_group.Update;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorRequest {
    @Null(groups = Create.class, message = "Id must not be provided during creation.")
    @NotNull(groups = Update.class, message = "Id is required during update.")
    Long id;

    @NotBlank(groups = {Create.class,Update.class}, message = "Color name is required.")
    @Size(max = 50, message = "Color name must not exceed 50 characters.")
    String name;

    @NotBlank(groups = {Create.class,Update.class}, message = "Hex code is required.")
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Hex code must be a valid color code (e.g., #FFFFFF or #FFF)."
    )
    String hexCode;
}
