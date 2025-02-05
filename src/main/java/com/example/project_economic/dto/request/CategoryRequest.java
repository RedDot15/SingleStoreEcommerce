package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
	@Null(groups = Create.class, message = "Id must not be provided during creation.")
	@NotNull(groups = Update.class, message = "Id is required during update.")
	Long id;

	@NotBlank(
			groups = {Create.class, Update.class},
			message = "Category name is required.")
	String name;
}
