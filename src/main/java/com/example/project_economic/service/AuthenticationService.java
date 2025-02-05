package com.example.project_economic.service;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.request.authentication.RefreshRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.dto.response.authentication.RefreshResponse;

public interface AuthenticationService {
  // Authenticate
  AuthenticationResponse authenticate(AuthenticationRequest request);

  // Refresh
  RefreshResponse refresh(RefreshRequest request);

  // Logout
  void logout();
}
