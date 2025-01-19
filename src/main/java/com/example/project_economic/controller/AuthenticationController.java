package com.example.project_economic.controller;

import com.example.project_economic.dto.request.authentication.AuthenticationRequest;
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
    public ResponseEntity<ResponseObject> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return buildResponse(
                HttpStatus.OK,
                "Authenticate successfully.",
                authenticationService.authenticate(request)
        );
    }

    @PostMapping("/my-token/invalidate")
    public ResponseEntity<ResponseObject> logout() {
        authenticationService.logout();
        return buildResponse(
                HttpStatus.OK,
                "Log out successfully.",
                null
        );
    }

    private ResponseEntity<ResponseObject> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(
                new ResponseObject(
                        status.is2xxSuccessful() ? "ok" : "failed",
                        message,
                        data
                )
        );
    }
}
