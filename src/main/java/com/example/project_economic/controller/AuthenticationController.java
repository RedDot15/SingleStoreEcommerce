package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
import com.example.project_economic.dto.request.authentication.RefreshRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
  AuthenticationService authenticationService;

  @GetMapping("/token/get")
  public ResponseEntity<ResponseObject> authenticate(
      @Valid @RequestBody AuthenticationRequest request) {
    return buildResponse(
        HttpStatus.OK, "Authenticate successfully.", authenticationService.authenticate(request));
  }

  @PostMapping("/token/refresh")
  public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody RefreshRequest request) {
    return buildResponse(
        HttpStatus.OK, "Authenticate successfully.", authenticationService.refresh(request));
  }

  @PostMapping("/my-token/invalidate")
  public ResponseEntity<ResponseObject> logout() {
    authenticationService.logout();
    return buildResponse(HttpStatus.OK, "Log out successfully.", null);
  }
}
