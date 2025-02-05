package com.example.project_economic.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.response.authentication.AuthenticationResponse;
import com.example.project_economic.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class AuthenticationControllerTest {

	MockMvc mockMvc;

	@Mock
	AuthenticationService authenticationServiceMock;

	@InjectMocks
	private AuthenticationController authenticationController;

	ObjectMapper objectMapper;
	AuthenticationRequest authenticationRequest;
	AuthenticationResponse authenticationResponse;

	public static final String USERNAME = "test";
	public static final String PASSWORD = "test";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REFRESH_TOKEN = "refreshToken";

	@BeforeEach
	void initData() {
		mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

		objectMapper = new ObjectMapper();

		authenticationRequest = AuthenticationRequest.builder()
				.username(USERNAME)
				.password(PASSWORD)
				.build();

		authenticationResponse = AuthenticationResponse.builder()
				.authenticated(true)
				.accessToken(ACCESS_TOKEN)
				.refreshToken(REFRESH_TOKEN)
				.build();
	}

	@Test
	public void authenticate_validRequest_success() throws Exception {
		// GIVEN
		String content = objectMapper.writeValueAsString(authenticationRequest);

		Mockito.when(authenticationServiceMock.authenticate(authenticationRequest))
				.thenReturn(authenticationResponse);
		// WHEN & THEN
		mockMvc.perform(get("/auth/token/get")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(content))
				.andExpect(status().isOk())
				.andExpect(jsonPath("status").value("ok"))
				.andExpect(jsonPath("message").value("Authenticate successfully."))
				.andExpect(jsonPath("data.accessToken").value(ACCESS_TOKEN))
				.andExpect(jsonPath("data.refreshToken").value(REFRESH_TOKEN));

		verify(authenticationServiceMock).authenticate(authenticationRequest);
	}
}
