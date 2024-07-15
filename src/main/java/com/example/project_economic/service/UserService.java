package com.example.project_economic.service;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();

    void save(UserRequest userRequest) throws Exception;
    void save(UserRegisterRequest userRegisterRequest);

    void deleteById(Long userId);

    UserEntity createUser(UserEntity userEntity) throws Exception;
    UserEntity findUserById(Long userId);
    UserEntity update(UserEntity userEntity, Long userId);

}
