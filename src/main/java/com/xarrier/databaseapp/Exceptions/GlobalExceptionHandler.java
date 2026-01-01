package com.xarrier.databaseapp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(
            UsernameAlreadyExistsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "USERNAME_ALREADY_EXISTS",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(
            EmailAlreadyExistsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "EMAIL_ALREADY_EXISTS",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(
            InvalidCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Invalid credentials",
                        "message", ex.getMessage()
                )
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", 400,
                        "error", "VALIDATION_FAILED",
                        "message", "Invalid request data",
                        "fields", fieldErrors
                )
        );
    }

}
