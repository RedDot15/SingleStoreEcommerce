package com.example.project_economic.service;

import com.example.project_economic.dto.request.UserChangePasswordRequest;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.UserResponse;
import java.util.Set;

public interface UserService {
  // Fetch
  Set<UserResponse> getAll();

  UserResponse getMyInfo();

  // Add/Update/Delete
  UserResponse add(UserRequest userRequest);

  UserResponse update(UserRequest userRequest);

  Long delete(Long id);

  // Change status
  UserResponse deactivate(Long id);

  UserResponse activate(Long id);

  // CLIENT service
  UserResponse register(UserRequest userRequest);

  UserResponse updateInformation(UserRequest userRequest);

  UserResponse changePassword(UserChangePasswordRequest request);
}
