package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangePasswordRequest {
  @NotNull(groups = Update.class, message = "Id is required.")
  Long id;

  @NotBlank(groups = Update.class, message = "Old password is required.")
  String oldPassword;

  @NotBlank(groups = Update.class, message = "Password is required.")
  @Size(min = 6, message = "Password must be at least {min} characters long.")
  String password;

  @NotBlank(groups = Update.class, message = "Confirm password is required.")
  String confirmPassword;
}
