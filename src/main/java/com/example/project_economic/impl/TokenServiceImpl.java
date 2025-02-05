package com.example.project_economic.impl;

import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.repository.InvalidatedTokenRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service
public class TokenServiceImpl implements TokenService {
	NimbusJwtDecoder nimbusJwtDecoder;
	UserRepository userRepository;
	InvalidatedTokenRepository invalidatedTokenRepository;

	@NonFinal
	@Value("${jwt.signer-key}")
	String SIGNER_KEY;

	@NonFinal
	@Value("${jwt.valid-duration}")
	Long VALID_DURATION;

	@NonFinal
	@Value("${jwt.refreshable-duration}")
	Long REFRESHABLE_DURATION;

	public String generateToken(UserEntity userEntity, Boolean isRefreshToken, String jti) {
		// Define Header
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		// Calculate expiration time based on token type
		Long duration = isRefreshToken ? REFRESHABLE_DURATION : VALID_DURATION;
		Date expirationTime = Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS));
		// Define Body: ClaimSet
		JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
				.subject(userEntity.getUsername())
				.issuer("reddot15.com")
				.issueTime(new Date())
				.expirationTime(expirationTime)
				.jwtID(isRefreshToken ? jti : null);

		if (!isRefreshToken) {
			claimsBuilder
					.claim("rid", jti)
					.claim("scope", buildScope(userEntity))
					.claim("uid", userEntity.getId());
		}

		JWTClaimsSet jwtClaimsSet = claimsBuilder.build();
		// Define Body: Payload
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

	public Jwt verifyToken(String token, Boolean isRefreshToken) {
		try {
			// Decode jwt (function include integrity verify & expiry verify)
			Jwt jwt = nimbusJwtDecoder.decode(token);
			// Validate token based on type
			String tokenId = isRefreshToken ? jwt.getClaim("jti") : jwt.getClaim("rid");
			if (Objects.isNull(tokenId) || invalidatedTokenRepository.existsById(tokenId)) {
				throw new JwtException("Invalid token");
			}
			if (userRepository.findActiveByUsername(jwt.getSubject()).isEmpty()) throw new JwtException("Invalid user");
			// Return jwt
			return jwt;
		} catch (JwtException e) {
			log.error("JWT decoding failed: {}", e.getMessage());
			throw e; // Re-throw to let Spring Security handle it
		}
	}

	private String buildScope(UserEntity userEntity) {
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
