package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.RoleResponse;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.RoleEntity;
import com.example.project_economic.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  // Add
  @Mapping(target = "roleEntitySet", ignore = true)
  @Mapping(target = "password", ignore = true)
  UserEntity toUserEntity(UserRequest userRequest);

  // Update
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "roleEntitySet", ignore = true)
  @Mapping(target = "password", ignore = true)
  void updateUserEntityFromRequest(@MappingTarget UserEntity userEntity, UserRequest userRequest);

  // Response
  @Mapping(target = "roleResponseSet", source = "roleEntitySet")
  UserResponse toUserResponse(UserEntity userEntity);

  @Mapping(target = "permissionResponseSet", source = "permissionEntitySet")
  RoleResponse toRoleResponse(RoleEntity roleEntity);
}
