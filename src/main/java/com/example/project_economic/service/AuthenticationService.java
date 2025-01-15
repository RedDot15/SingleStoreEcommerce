package com.example.project_economic.service;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;

public interface AuthenticationService {
    // Authenticate
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
