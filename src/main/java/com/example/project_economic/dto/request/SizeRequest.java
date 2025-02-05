package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
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
public class SizeRequest {
	@Null(groups = Create.class, message = "Id must not be provided during creation.")
	@NotNull(groups = Update.class, message = "Id is required during update.")
	Long id;

	@NotBlank(
			groups = {Create.class, Update.class},
			message = "Size name is required.")
	@Size(max = 50, message = "Size name must not exceed {max} characters.")
	String name;
}
