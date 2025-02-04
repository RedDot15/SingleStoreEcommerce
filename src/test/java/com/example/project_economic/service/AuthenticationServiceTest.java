package com.example.project_economic.service;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.entity.RoleEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.impl.AuthenticationServiceImpl;
import com.example.project_economic.repository.InvalidatedTokenRepository;
import com.example.project_economic.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class AuthenticationServiceTest {

    @Mock
    UserRepository userRepositoryMock;
    @Mock
    TokenService tokenServiceMock;
    @Mock
    PasswordEncoder passwordEncoderMock;
    @Mock
    InvalidatedTokenRepository invalidatedTokenRepositoryMock;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    AuthenticationRequest authenticationRequest;
    UserEntity userEntity;

    private static final Long USER_ID = 1L;
    private static final String PASSWORD_HASH = "hashedPassword";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "test@gmail.com";
    private static final String PHONE_NUMBER = "0123456789";
    private static final String ADDRESS = "test";
    private static final String ROLE_NAME = "USER";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";


    @BeforeEach
    void initData(){
        authenticationRequest = AuthenticationRequest.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name(ROLE_NAME)
                .description("User role.")
                .build();

        userEntity = UserEntity.builder()
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
    }

    @Test
    public void authenticate_validRequest_success() {
        // GIVEN
        when(userRepositoryMock.findActiveByUsername(USERNAME))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoderMock.matches(PASSWORD, PASSWORD_HASH))
                .thenReturn(true);
        when(tokenServiceMock.generateToken(eq(userEntity), eq(true), anyString()))
                .thenReturn(REFRESH_TOKEN);
        when(tokenServiceMock.generateToken(eq(userEntity), eq(false), anyString()))
                .thenReturn(ACCESS_TOKEN);
        // WHEN
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getAuthenticated()).isTrue();
        assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN);

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
                assertThrows(AppException.class, () -> authenticationService.authenticate(authenticationRequest));
        // THEN
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepositoryMock).findActiveByUsername(USERNAME);
        verifyNoInteractions(passwordEncoderMock, tokenServiceMock);
    }

    @Test
    public void authenticate_invalidPassword_throwsException() {
        // GIVEN
        when(userRepositoryMock.findActiveByUsername(USERNAME))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoderMock.matches(PASSWORD, PASSWORD_HASH))
                .thenReturn(false);
        // WHEN
        AppException exception =
                assertThrows(AppException.class, () -> authenticationService.authenticate(authenticationRequest));
        // THEN
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(userRepositoryMock).findActiveByUsername(USERNAME);
        verify(passwordEncoderMock).matches(PASSWORD, PASSWORD_HASH);
        verifyNoInteractions(tokenServiceMock);
    }
}
