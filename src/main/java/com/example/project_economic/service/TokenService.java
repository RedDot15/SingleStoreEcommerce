package com.example.project_economic.service;

import com.example.project_economic.entity.UserEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface TokenService {
    //Generate token
    String generateToken(UserEntity userEntity, Boolean isRefreshToken, String jti);

    // Verify token
    Jwt verifyToken(String token, Boolean isRefreshToken);

}
