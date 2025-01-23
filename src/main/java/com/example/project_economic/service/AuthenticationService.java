package com.example.project_economic.service;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.request.authentication.RefreshRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthenticationService {
    // Authenticate
    AuthenticationResponse authenticate(AuthenticationRequest request);

    // Refresh
    AuthenticationResponse refresh(RefreshRequest request);

    // Logout
    void logout();

    // Verify token
    Jwt verifyToken(String token, Boolean isRefreshToken);
}
