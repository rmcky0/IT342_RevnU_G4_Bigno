package com.revnu.backend.shared.exception;

public record ApiResponse(
        boolean success,
        Object data,
        ErrorDetail error,
        String timestamp
        ) {

}
