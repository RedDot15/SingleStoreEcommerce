package com.example.project_economic.impl;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.request.authentication.RefreshRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.entity.InvalidatedTokenEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.repository.InvalidatedTokenRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.AuthenticationService;
import com.example.project_economic.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    TokenService tokenService;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    Long REFRESHABLE_DURATION;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Fetch
        UserEntity userEntity = userRepository.findActiveByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Authenticate
        boolean authenticated = passwordEncoder.matches(request.getPassword(), userEntity.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        // Generate token
        String uuid = UUID.randomUUID().toString();
        String refreshToken = tokenService.generateToken(userEntity, true, uuid);
        String accessToken = tokenService.generateToken(userEntity, false, uuid);
        // Return token
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationResponse refresh(RefreshRequest request) {
        // Verify token
        Jwt jwt = tokenService.verifyToken(request.getRefreshToken(), true);
        // Get token information
        UserEntity userEntity = userRepository.findActiveByUsername(jwt.getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        String jti = jwt.getClaim("jti");
        Date expiryTime = Date.from(jwt.getClaim("exp"));
        // Build & Save invalid token
        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedTokenEntity);
        // Generate new token
        String uuid = UUID.randomUUID().toString();
        String refreshToken = tokenService.generateToken(userEntity, true, uuid);
        String accessToken = tokenService.generateToken(userEntity, false, uuid);
        // Return token
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public void logout() {
        // Get Jwt token from Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        // Get token information
        String jti = jwt.getClaim("rid");
        Date expiryTime = Date.from(
                Instant.from(jwt.getClaim("iat")).plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
        );
        // Build & Save invalid token
        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedTokenEntity);
    }
}
