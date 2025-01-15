package com.example.project_economic.impl;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.AuthenticationService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

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
                .claim("scope", buildScope(userEntity))
                .claim("id", userEntity.getId())
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
