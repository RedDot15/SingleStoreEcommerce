package com.example.project_economic.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
	@NotBlank(message = "Username is required.")
	String username;

	@NotBlank(message = "Password is required.")
	String password;
}
