package com.revnu.backend.shared.exception;

import java.time.Instant;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDetail detail = new ErrorDetail(
                "VALID-001",
                "Validation failed",
                ex.getMessage()
        );
        ApiErrorResponse response = new ApiErrorResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorDetail detail = new ErrorDetail(
                "BUSINESS-001",
                "Business rule violation",
                ex.getMessage()
        );
        ApiErrorResponse response = new ApiErrorResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurity(SecurityException ex) {
        ErrorDetail detail = new ErrorDetail(
                "AUTH-003",
                "Insufficient permissions",
                ex.getMessage()
        );
        ApiErrorResponse response = new ApiErrorResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.warning("Method not allowed: " + request.getMethod() + " " + request.getRequestURI() + " -> supported: " + String.join(",", ex.getSupportedMethods() == null ? new String[]{} : ex.getSupportedMethods()));

        ErrorDetail detail = new ErrorDetail(
                "HTTP-405",
                "Method not allowed",
                "Request method '" + request.getMethod() + "' is not supported for this endpoint."
        );
        ApiErrorResponse response = new ApiErrorResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
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
        ApiErrorResponse response = new ApiErrorResponse(false, null, detail, getCurrentTimestamp());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
