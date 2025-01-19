package com.example.project_economic.config.security;

import com.example.project_economic.repository.InvalidatedTokenRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class CustomJwtDecoder implements JwtDecoder {
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;

    @NonFinal
    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

    @Override
    public Jwt decode(String token) {
        try{
            // Initialize for nimbusJwtDecoder
            if (Objects.isNull(nimbusJwtDecoder)) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }
            // Decode jwt (function include integrity verify & expiry verify)
            Jwt jwt = nimbusJwtDecoder.decode(token);
            // Verify validate token
            if (invalidatedTokenRepository.existsById(jwt.getClaim("jti")))
                throw new JwtException("Invalid JWT");
            // Return jwt
            return jwt;
        } catch (JwtException e) {
            log.error("JWT decoding failed: {}", e.getMessage());
            throw e; // Re-throw to let Spring Security handle it
        }
    }
}