package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    // Add
    UserEntity toUserEntity(UserRequest userRequest);
    // Update
    @Mapping(target = "password", ignore = true)
    void updateUserEntityFromRequest(@MappingTarget UserEntity userEntity, UserRequest userRequest);
    // Response
    UserResponse toUserResponse(UserEntity userEntity);
}
