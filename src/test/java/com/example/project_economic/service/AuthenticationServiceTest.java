package com.example.project_economic.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.request.authentication.RefreshRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.dto.response.authentication.RefreshResponse;
import com.example.project_economic.entity.InvalidatedTokenEntity;
import com.example.project_economic.entity.RoleEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.impl.AuthenticationServiceImpl;
import com.example.project_economic.repository.InvalidatedTokenRepository;
import com.example.project_economic.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class AuthenticationServiceTest {
  @Mock UserRepository userRepositoryMock;

  @Mock TokenService tokenServiceMock;

  @Mock PasswordEncoder passwordEncoderMock;

  @Mock InvalidatedTokenRepository invalidatedTokenRepositoryMock;

  @Mock SecurityContext securityContextMock;

  @Mock Authentication authenticationMock;

  @InjectMocks AuthenticationServiceImpl authenticationService;

  // Request
  AuthenticationRequest authenticationRequest;
  RefreshRequest refreshRequest;
  // Response
  UserEntity userEntity;
  Jwt refreshJwt;
  InvalidatedTokenEntity invalidatedTokenEntity;
  Jwt accessJwt;

  private static final Long USER_ID = 1L;
  private static final String PASSWORD_HASH = "hashedPassword";
  private static final String USERNAME = "testUser";
  private static final String PASSWORD = "testPassword";
  private static final String EMAIL = "test@gmail.com";
  private static final String PHONE_NUMBER = "0123456789";
  private static final String ADDRESS = "test";
  private static final String ROLE_NAME = "USER";

  private static final String ACCESS_TOKEN_REQUEST = "accessTokenRequest";
  private static final String REFRESH_TOKEN_REQUEST = "refreshTokenRequest";

  private static final String ACCESS_TOKEN_RESPONSE = "accessTokenResponse";
  private static final String REFRESH_TOKEN_RESPONSE = "refreshTokenResponse";

  private static final Date ACCESS_TOKEN_IAT = Date.from(Instant.now());

  private static final String REFRESH_TOKEN_ID = "refreshTokenId";
  private static final Date REFRESH_TOKEN_EXPIRY =
      Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

  @BeforeEach
  void initData() {
    // Request
    authenticationRequest =
        AuthenticationRequest.builder().username(USERNAME).password(PASSWORD).build();

    refreshRequest = RefreshRequest.builder().refreshToken(REFRESH_TOKEN_REQUEST).build();

    // Response
    RoleEntity roleEntity =
        RoleEntity.builder().id(1L).name(ROLE_NAME).description("User role.").build();

    userEntity =
        UserEntity.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD_HASH)
            .roleEntitySet(Set.of(roleEntity))
            .email(EMAIL)
            .phoneNumber(PHONE_NUMBER)
            .address(ADDRESS)
            .isActive(true)
            .isDeleted(false)
            .build();

    refreshJwt =
        Jwt.withTokenValue(REFRESH_TOKEN_REQUEST)
            .headers(h -> h.put("alg", "HS512"))
            .subject(USERNAME)
            .jti(REFRESH_TOKEN_ID)
            .expiresAt(REFRESH_TOKEN_EXPIRY.toInstant())
            .build();

    invalidatedTokenEntity =
        InvalidatedTokenEntity.builder()
            .id(REFRESH_TOKEN_ID)
            .expiryTime(REFRESH_TOKEN_EXPIRY)
            .build();

    accessJwt =
        Jwt.withTokenValue(ACCESS_TOKEN_REQUEST)
            .headers(h -> h.put("alg", "HS512"))
            .subject(USERNAME)
            .issuedAt(ACCESS_TOKEN_IAT.toInstant())
            .build();
  }

  @Test
  public void authenticate_validRequest_success() {
    // GIVEN
    when(userRepositoryMock.findActiveByUsername(USERNAME)).thenReturn(Optional.of(userEntity));
    when(passwordEncoderMock.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);
    when(tokenServiceMock.generateToken(eq(userEntity), eq(true), anyString()))
        .thenReturn(REFRESH_TOKEN_RESPONSE);
    when(tokenServiceMock.generateToken(eq(userEntity), eq(false), anyString()))
        .thenReturn(ACCESS_TOKEN_RESPONSE);
    // WHEN
    AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
    // THEN
    assertThat(response).isNotNull();
    assertThat(response.getAuthenticated()).isTrue();
    assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN_RESPONSE);
    assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN_RESPONSE);

    verify(userRepositoryMock).findActiveByUsername(USERNAME);
    verify(passwordEncoderMock).matches(PASSWORD, PASSWORD_HASH);
    verify(tokenServiceMock, times(2)).generateToken(any(), anyBoolean(), any());
  }

  @Test
  public void authenticate_userNotFound_throwsException() {
    // GIVEN
    when(userRepositoryMock.findActiveByUsername(USERNAME)).thenReturn(Optional.empty());
    // WHEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.authenticate(authenticationRequest));
    // THEN
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

    verify(userRepositoryMock).findActiveByUsername(USERNAME);
    verifyNoInteractions(passwordEncoderMock, tokenServiceMock);
  }

  @Test
  public void authenticate_invalidPassword_throwsException() {
    // GIVEN
    when(userRepositoryMock.findActiveByUsername(USERNAME)).thenReturn(Optional.of(userEntity));
    when(passwordEncoderMock.matches(PASSWORD, PASSWORD_HASH)).thenReturn(false);
    // WHEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.authenticate(authenticationRequest));
    // THEN
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

    verify(userRepositoryMock).findActiveByUsername(USERNAME);
    verify(passwordEncoderMock).matches(PASSWORD, PASSWORD_HASH);
    verifyNoInteractions(tokenServiceMock);
  }

  @Test
  public void refresh_validRequest_success() {
    // GIVEN
    when(tokenServiceMock.verifyToken(REFRESH_TOKEN_REQUEST, true)).thenReturn(refreshJwt);
    when(userRepositoryMock.findActiveByUsername(USERNAME)).thenReturn(Optional.of(userEntity));
    when(invalidatedTokenRepositoryMock.save(any())).thenReturn(invalidatedTokenEntity);
    when(tokenServiceMock.generateToken(eq(userEntity), eq(true), anyString()))
        .thenReturn(REFRESH_TOKEN_RESPONSE);
    when(tokenServiceMock.generateToken(eq(userEntity), eq(false), anyString()))
        .thenReturn(ACCESS_TOKEN_RESPONSE);
    // WHEN
    RefreshResponse response = authenticationService.refresh(refreshRequest);
    // THEN
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN_RESPONSE);
    assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN_RESPONSE);

    verify(tokenServiceMock).verifyToken(REFRESH_TOKEN_REQUEST, true);
    verify(userRepositoryMock).findActiveByUsername(USERNAME);
    verify(invalidatedTokenRepositoryMock).save(any());
    verify(tokenServiceMock, times(2)).generateToken(eq(userEntity), anyBoolean(), anyString());
  }

  @Test
  public void refresh_userNotFound_throwsException() {
    // GIVEN
    when(tokenServiceMock.verifyToken(REFRESH_TOKEN_REQUEST, true)).thenReturn(refreshJwt);
    when(userRepositoryMock.findActiveByUsername(USERNAME)).thenReturn(Optional.empty());
    // WHEN
    AppException exception =
        assertThrows(AppException.class, () -> authenticationService.refresh(refreshRequest));
    // THEN
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

    verify(tokenServiceMock).verifyToken(REFRESH_TOKEN_REQUEST, true);
    verify(userRepositoryMock).findActiveByUsername(USERNAME);
    verifyNoInteractions(invalidatedTokenRepositoryMock);
  }

  @Test
  public void logout_validRequest_success() {
    // GIVEN
    SecurityContextHolder.setContext(securityContextMock);
    when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
    when(authenticationMock.getPrincipal()).thenReturn(accessJwt);
    ReflectionTestUtils.setField(authenticationService, "REFRESHABLE_DURATION", 3600L);
    when(invalidatedTokenRepositoryMock.save(any())).thenReturn(invalidatedTokenEntity);
    // WHEN
    authenticationService.logout();
    // THEN
    verify(securityContextMock).getAuthentication();
    verify(authenticationMock).getPrincipal();
    verify(invalidatedTokenRepositoryMock).save(any());
  }
}
