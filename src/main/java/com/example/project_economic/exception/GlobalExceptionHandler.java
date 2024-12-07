package com.example.project_economic.exception;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("failed", "An unexpected error occurred.", null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseObject> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("failed", e.getMessage(), null));
    }

    // Used in fetch inactive entity
    // Used in confirm password unmatch
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", e.getMessage(), null));
    }

    // Used in entity not meet requirement to activate
    @ExceptionHandler(ActivationException.class)
    public ResponseEntity<ResponseObject> handleActivationException(ActivationException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ResponseObject("failed", e.getMessage(), null));
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ResponseObject> handleWrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", e.getMessage(), null));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ResponseObject> handleDuplicateException(DuplicateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseObject("failed", e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", errorMessage, null));
    }
}
