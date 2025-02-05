package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageRequest {
	@Null(groups = Create.class, message = "Id must not be provided during creation.")
	@NotNull(groups = Update.class, message = "Id is required during update.")
	Long id;

	@NotNull(
			groups = {Create.class, Update.class},
			message = "File image is required.")
	@Size(max = 1048576, message = "File size must not exceed 1 MB.") // Custom validation needed for file size
	MultipartFile fileImage;

	@NotNull(groups = Create.class, message = "Product ID is required.")
	@Positive(message = "Product ID must be greater than 0.")
	Long productId;

	@NotNull(groups = Create.class, message = "Color ID is required.")
	@Positive(message = "Color ID must be greater than 0.")
	Long colorId;
}
