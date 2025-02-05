package com.example.project_economic.dto.response;

import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
	Long id;

	String name;

	String description;

	Set<PermissionResponse> permissionResponseSet;
}
