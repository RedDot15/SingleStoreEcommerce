package com.example.project_economic.service;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import org.apache.catalina.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    Set<UserResponse> getAll();

    Boolean existsById(Long id);

    Boolean existsByUsernameOrEmail(String username, String email);

    Boolean existsByUsernameOrEmailExceptId(String username, String email, Long id);

    UserResponse getFirstById(Long id);

    UserResponse create(UserRequest userRequest);

    UserResponse update(UserRequest userRequest);

    void save(UserRegisterRequest userRegisterRequest);

    void delete(Long id);

    UserResponse deactivate(Long id);

    UserResponse activate(Long id);

    UserEntity createUser(UserEntity userEntity) throws Exception;


    UserEntity update(UserEntity userEntity, Long userId);

}
