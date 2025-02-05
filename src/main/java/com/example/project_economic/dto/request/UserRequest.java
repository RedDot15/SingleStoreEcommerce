package com.example.project_economic.dto.request;

import com.example.project_economic.validation.annotation.AllOrNone;
import com.example.project_economic.validation.annotation.Match;
import com.example.project_economic.validation.group.Admin;
import com.example.project_economic.validation.group.Client;
import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.*;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllOrNone(
    groups = Update.class,
    fields = {"password", "confirmPassword"},
    message = "These fields {fields} must be all null or all not null.")
@Match(
    fields = {"password", "confirmPassword"},
    message = "These fields {fields} must match.")
public class UserRequest {
  @Null(groups = Create.class, message = "Id must not be provided during creation.")
  @NotNull(groups = Update.class, message = "Id is required during update.")
  Long id;

  @NotBlank(groups = Create.class, message = "Username is required.")
  @Size(min = 3, max = 20, message = "Username must be between {min} and {max} characters.")
  @Null(
      groups = Update.class,
      message = "Username must not be provided during update since username should not be updated.")
  String username;

  @NotBlank(
      groups = {Create.class, Update.class},
      message = "Email is required.")
  @Email(message = "Email must be a valid email address.")
  String email;

  @NotBlank(
      groups = {Create.class},
      message = "Password is required.")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$",
      message =
          "Password must contains at least 8 characters and at most 20 characters."
              + "Password must contains at least one digit."
              + "Password must contains at least one upper case alphabet."
              + "Password must contains at least one lower case alphabet."
              + "Password must contains at least one special character which includes !@#$%&*()-+=^."
              + "Password must not contain any white space.")
  String password;

  @NotBlank(
      groups = {Create.class},
      message = "Confirm password is required.")
  String confirmPassword;

  @Null(groups = Client.class, message = "Role must not be provided as client.")
  @NotEmpty(groups = Admin.class, message = "Roles is required.")
  Set<Long> roleIdSet;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Phone number is required.")
  @Pattern(
      regexp = "^(\\+?[0-9]{10,15})?$",
      message =
          "Phone number must be between 10 and 15 digits and can include an optional leading '+'.")
  String phoneNumber;

  @NotNull(
      groups = {Create.class, Update.class},
      message = "Address is required.")
  @Size(max = 255, message = "Address must not exceed {max} characters.")
  String address;
}
