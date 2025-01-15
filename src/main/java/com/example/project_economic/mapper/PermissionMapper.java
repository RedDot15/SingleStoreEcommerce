package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.PermissionRequest;
import com.example.project_economic.dto.response.PermissionResponse;
import com.example.project_economic.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    // Add
    PermissionEntity toPermissionEntity(PermissionRequest permissionRequest);
    // Update
    void updatePermissionEntityFromRequest(@MappingTarget PermissionEntity permissionEntity, PermissionRequest permissionRequest);
    // Response
    PermissionResponse toPermissionResponse(PermissionEntity permissionEntity);
}
