package com.revnu.backend.shared.exception;

import java.time.Instant;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    private String getCurrentTimestamp() {
        return Instant.now().toString();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDetail detail = new ErrorDetail(
                "VALID-001",
                "Validation failed",
                ex.getMessage()
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex) {
        ErrorDetail detail = new ErrorDetail(
                "BUSINESS-001",
                "Business rule violation",
                ex.getMessage()
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse> handleSecurity(SecurityException ex) {
        ErrorDetail detail = new ErrorDetail(
                "AUTH-003",
                "Insufficient permissions",
                ex.getMessage()
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.warning("Method not allowed: " + request.getMethod() + " " + request.getRequestURI() + " -> supported: " + String.join(",", ex.getSupportedMethods() == null ? new String[]{} : ex.getSupportedMethods()));

        ErrorDetail detail = new ErrorDetail(
                "HTTP-405",
                "Method not allowed",
                "Request method '" + request.getMethod() + "' is not supported for this endpoint."
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        ErrorDetail detail = new ErrorDetail(
                "FILE-413",
                "Upload too large",
                "Uploaded file exceeds the maximum allowed size."
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> handleResponseStatus(ResponseStatusException ex) {
        ErrorDetail detail = new ErrorDetail(
                "HTTP-" + ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : "Request error",
                null
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");
        ErrorDetail detail = new ErrorDetail("VALID-001", "Validation failed", message);
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ErrorDetail detail = new ErrorDetail("AUTH-003", "Insufficient permissions",
                "You don't have permission to perform this action.");
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.severe("UNCAUGHT EXCEPTION: " + ex.getClass().getName() + " - " + ex.getMessage());
        logger.severe("Request: " + (request != null ? request.getMethod() + " " + request.getRequestURI() : "unknown"));
        ex.printStackTrace();

        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTrace.append("\n  at ").append(element.toString());
        }
        logger.severe("Stack trace:" + stackTrace.toString());

        ErrorDetail detail = new ErrorDetail(
                "SYSTEM-001",
                "Internal server error",
                "An unexpected system error occurred. Please try again later."
        );
        ApiResponse response = new ApiResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
