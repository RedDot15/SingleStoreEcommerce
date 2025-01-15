package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.RoleRequest;
import com.example.project_economic.dto.response.RoleResponse;
import com.example.project_economic.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    // Add
    @Mapping(target = "permissionEntitySet", ignore = true)
    RoleEntity toRoleEntity(RoleRequest roleRequest);
    // Update
    @Mapping(target = "permissionEntitySet", ignore = true)
    void updateRoleEntityFromRequest(@MappingTarget RoleEntity roleEntity, RoleRequest roleRequest);
    // Response
    @Mapping(target = "permissionResponseSet", source = "permissionEntitySet")
    RoleResponse toRoleResponse(RoleEntity roleEntity);
}
