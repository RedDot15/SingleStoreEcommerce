package com.example.project_economic.dto.request;

import com.example.project_economic.validation_group.ChangePassword;
import com.example.project_economic.validation_group.Client;
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
public class UserRequest {
    @Null(groups = Create.class, message = "Id must not be provided during creation.")
    @NotNull(groups = Update.class, message = "Id is required during update.")
    Long id;

    @NotBlank(groups = Create.class, message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    @Null(groups = Update.class, message = "Username must not be provided during update since username should not be updated.")
    String username;

    @NotBlank(groups = Create.class, message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    String email;

    @NotBlank(groups = ChangePassword.class, message = "Old password is required.")
    String oldPassword;

    @NotBlank(groups = {Create.class,ChangePassword.class}, message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    String password;

    @NotBlank(groups = {Create.class,ChangePassword.class}, message = "Confirm password is required.")
    String confirmPassword;

    @Null(groups = Client.class, message = "Role must not be provided as client.")
    String role;

    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be between 10 and 15 digits and can include an optional leading '+'."
    )
    String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    String address;
}
