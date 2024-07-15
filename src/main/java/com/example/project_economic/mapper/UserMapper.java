package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(UserEntity userEntity);
    UserEntity toUserEntity(UserRequest userRequest);
    UserEntity toUserEntity(UserRegisterRequest userRegisterRequest);
}
