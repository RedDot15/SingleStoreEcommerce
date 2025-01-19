package com.example.project_economic.impl;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.entity.InvalidatedTokenEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.repository.InvalidatedTokenRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

    @NonFinal
    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

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
        String token = generateToken(userEntity);
        // Return token
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public void logout() {
        // Get Jwt token from Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        // Get token information
        String jti = jwt.getClaim("jti");
        Date expiryTime = Date.from(jwt.getClaim("exp"));
        // Save invalid token
        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedTokenEntity);
    }

    private String generateToken(UserEntity userEntity) {
        // Define Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // Define Body: Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getUsername())
                .issuer("reddot15.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(userEntity))
                .claim("uid", userEntity.getId())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        // Define JWSObject
        JWSObject jwsObject = new JWSObject(header, payload);
        // Sign JWSObject & Return
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(UserEntity userEntity){
        StringJoiner scope = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(userEntity.getRoleEntitySet()))
            userEntity.getRoleEntitySet().forEach(roleEntity -> {
                scope.add("ROLE_" + roleEntity.getName());
                if (!CollectionUtils.isEmpty(roleEntity.getPermissionEntitySet()))
                    roleEntity.getPermissionEntitySet().forEach(permissionEntity -> {
                        scope.add(permissionEntity.getName());
                    });
            });
        return scope.toString();
    }
}
