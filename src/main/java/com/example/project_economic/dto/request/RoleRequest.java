package com.example.project_economic.dto.request;

import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @Null(groups = Create.class, message = "Id must not be provided during create.")
    @NotNull(groups = Update.class, message = "Id is required during update.")
    Long id;

    @NotBlank(groups = {Create.class, Update.class}, message = "Name is required.")
    String name;

    @NotNull(groups = {Create.class, Update.class}, message = "Description is required.")
    @Size(max = 500, message = "Description must not exceed {max} characters.")
    String description;

    @NotNull(groups = {Create.class, Update.class}, message = "Permissions is required.")
    Set<Long> permissionIdSet;
}
