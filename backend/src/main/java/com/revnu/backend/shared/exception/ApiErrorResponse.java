package com.revnu.backend.shared.exception;

public record ApiErrorResponse(
        boolean success,
        Object data,
        ErrorDetail error,
        String timestamp
        ) {

}
